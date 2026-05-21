package com.fruitmkt.dao;

import com.fruitmkt.dao.base.BaseDAO;
import com.fruitmkt.model.entity.Cart;
import com.fruitmkt.model.entity.CartItem;
import java.sql.*;
import java.util.*;

/**
 * CartDAO — DAO cho entity Cart.
 *
 * QUY TẮC:
 *   - Chỉ chứa SQL, không chứa business logic
 *   - Dùng PreparedStatement, KHÔNG nối chuỗi SQL
 *   - Mỗi method ném SQLException để Service xử lý
 *   - Dùng try-with-resources cho Connection + PreparedStatement
 *
 * @author fruitmkt-team
 */
public class CartDAO extends BaseDAO {

    /**
     * Tìm giỏ hàng theo ID khách hàng.
     */
    public List<Cart> findByCustomer(int customerId) throws SQLException {
        List<Cart> list = new ArrayList<>();
        String sql = "SELECT * FROM cart WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Tạo giỏ hàng mới cho khách hàng và trả về cart_id tự sinh.
     */
    public int createForCustomer(int customerId) throws SQLException {
        String sql = "INSERT INTO cart (customer_id, created_at, updated_at) VALUES (?, GETDATE(), GETDATE())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Tạo giỏ hàng thất bại, không lấy được mã khóa tự tăng.");
    }

    /**
     * Thêm sản phẩm vào giỏ hàng. Nếu sản phẩm đã tồn tại, cộng dồn số lượng.
     */
    public Cart addItem(int cartId, int variantId, int quantity) throws SQLException {
        String selectSql = "SELECT cart_item_id, quantity FROM cart_items WHERE cart_id = ? AND variant_id = ?";
        int existingCartItemId = -1;
        int existingQuantity = 0;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, variantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    existingCartItemId = rs.getInt("cart_item_id");
                    existingQuantity = rs.getInt("quantity");
                }
            }
        }

        if (existingCartItemId != -1) {
            updateItemQuantity(existingCartItemId, existingQuantity + quantity);
        } else {
            String insertSql = "INSERT INTO cart_items (cart_id, variant_id, quantity, added_at) VALUES (?, ?, ?, GETDATE())";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setInt(1, cartId);
                ps.setInt(2, variantId);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            }
        }
        
        touchCart(cartId);
        return findCartById(cartId);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng.
     */
    public void updateItemQuantity(int cartItemId, int quantity) throws SQLException {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, cartItemId);
            ps.executeUpdate();
            
            int cartId = findCartIdByItemId(cartItemId);
            if (cartId != -1) {
                touchCart(cartId);
            }
        }
    }

    /**
     * Xóa sản phẩm khỏi giỏ hàng.
     */
    public Cart removeItem(int cartItemId) throws SQLException {
        int cartId = findCartIdByItemId(cartItemId);
        if (cartId != -1) {
            String sql = "DELETE FROM cart_items WHERE cart_item_id = ?";
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, cartItemId);
                ps.executeUpdate();
            }
            touchCart(cartId);
            return findCartById(cartId);
        }
        return null;
    }

    /**
     * Xóa sạch toàn bộ sản phẩm trong giỏ hàng.
     */
    public Cart clearCart(int cartId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE cart_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
        touchCart(cartId);
        return findCartById(cartId);
    }

    /**
     * Lấy danh sách sản phẩm trong giỏ hàng.
     */
    public List<CartItem> findItems(int cartId) throws SQLException {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT * FROM cart_items WHERE cart_id = ? ORDER BY added_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapCartItemRow(rs));
                }
            }
        }
        return list;
    }

    private Cart findCartById(int cartId) throws SQLException {
        String sql = "SELECT * FROM cart WHERE cart_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private void touchCart(int cartId) throws SQLException {
        String sql = "UPDATE cart SET updated_at = GETDATE() WHERE cart_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    private int findCartIdByItemId(int cartItemId) throws SQLException {
        String sql = "SELECT cart_id FROM cart_items WHERE cart_item_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cart_id");
                }
            }
        }
        return -1;
    }

    private CartItem mapCartItemRow(ResultSet rs) throws SQLException {
        CartItem ci = new CartItem();
        ci.setCartItemId(rs.getInt("cart_item_id"));
        ci.setCartId(rs.getInt("cart_id"));
        ci.setVariantId(rs.getInt("variant_id"));
        ci.setQuantity(rs.getInt("quantity"));
        
        Timestamp addedAtVal = rs.getTimestamp("added_at");
        if (addedAtVal != null) {
            ci.setAddedAt(addedAtVal.toLocalDateTime());
        }
        return ci;
    }

    /** Ánh xạ ResultSet -> Cart — gọi trong mọi query SELECT */
    private Cart mapRow(ResultSet rs) throws SQLException {
        Cart c = new Cart();
        c.setCartId(rs.getInt("cart_id"));
        c.setCustomerId(rs.getInt("customer_id"));
        
        Timestamp createdAtVal = rs.getTimestamp("created_at");
        if (createdAtVal != null) {
            c.setCreatedAt(createdAtVal.toLocalDateTime());
        }
        
        Timestamp updatedAtVal = rs.getTimestamp("updated_at");
        if (updatedAtVal != null) {
            c.setUpdatedAt(updatedAtVal.toLocalDateTime());
        }
        return c;
    }
}
