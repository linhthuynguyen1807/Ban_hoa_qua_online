package com.fruitmkt.dao;

import com.fruitmkt.dao.base.BaseDAO;
import com.fruitmkt.model.entity.ShopProfile;
import java.sql.*;
import java.util.*;

/**
 * ShopProfileDAO — DAO cho entity ShopProfile.
 *
 * QUY TẮC:
 *   - Chỉ chứa SQL, không chứa business logic
 *   - Dùng PreparedStatement, KHÔNG nối chuỗi SQL
 *   - Mỗi method ném SQLException để Service xử lý
 *   - Dùng try-with-resources cho Connection + PreparedStatement
 *
 * @author fruitmkt-team
 */
public class ShopProfileDAO extends BaseDAO {

    /**
     * Tìm shop profile theo ID người dùng.
     */
    public List<ShopProfile> findByUserId(int userId) throws SQLException {
        List<ShopProfile> list = new ArrayList<>();
        String sql = "SELECT * FROM shop_owner_profiles WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Lấy tất cả shop profile theo trạng thái duyệt.
     */
    public List<ShopProfile> findAll(String approvalStatus) throws SQLException {
        List<ShopProfile> list = new ArrayList<>();
        String sql = (approvalStatus == null) 
            ? "SELECT * FROM shop_owner_profiles" 
            : "SELECT * FROM shop_owner_profiles WHERE approval_status = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (approvalStatus != null) {
                ps.setString(1, approvalStatus);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Lưu mới một shop profile và trả về ID tự sinh.
     */
    public int save(ShopProfile profile) throws SQLException {
        String sql = "INSERT INTO shop_owner_profiles (user_id, shop_name, shop_description, approval_status, rejection_reason, approved_at, delivery_address, rating, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getShopName());
            ps.setString(3, profile.getShopDescription());
            ps.setString(4, profile.getApprovalStatus() != null ? profile.getApprovalStatus() : "PENDING");
            ps.setString(5, profile.getRejectionReason());
            ps.setTimestamp(6, profile.getApprovedAt() != null ? Timestamp.valueOf(profile.getApprovedAt()) : null);
            ps.setString(7, profile.getDeliveryAddress());
            ps.setBigDecimal(8, profile.getRating() != null ? profile.getRating() : java.math.BigDecimal.ZERO);
            
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    profile.setProfileId(generatedId);
                    return generatedId;
                }
            }
        }
        throw new SQLException("Lỗi khi thêm mới shop profile, không lấy được khóa tự tăng.");
    }

    /**
     * Cập nhật thông tin shop profile.
     */
    public void update(ShopProfile profile) throws SQLException {
        String sql = "UPDATE shop_owner_profiles SET shop_name = ?, shop_description = ?, approval_status = ?, rejection_reason = ?, approved_at = ?, delivery_address = ?, rating = ?, updated_at = GETDATE() WHERE profile_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile.getShopName());
            ps.setString(2, profile.getShopDescription());
            ps.setString(3, profile.getApprovalStatus());
            ps.setString(4, profile.getRejectionReason());
            ps.setTimestamp(5, profile.getApprovedAt() != null ? Timestamp.valueOf(profile.getApprovedAt()) : null);
            ps.setString(6, profile.getDeliveryAddress());
            ps.setBigDecimal(7, profile.getRating());
            ps.setInt(8, profile.getProfileId());
            ps.executeUpdate();
        }
    }

    /**
     * Duyệt hoặc từ chối phê duyệt shop.
     */
    public void updateApprovalStatus(int profileId, String status, String rejectionReason) throws SQLException {
        String sql = "UPDATE shop_owner_profiles SET approval_status = ?, rejection_reason = ?, approved_at = ?, updated_at = GETDATE() WHERE profile_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, rejectionReason);
            ps.setTimestamp(3, "APPROVED".equals(status) ? new Timestamp(System.currentTimeMillis()) : null);
            ps.setInt(4, profileId);
            ps.executeUpdate();
        }
    }

    /** Ánh xạ ResultSet -> ShopProfile — gọi trong mọi query SELECT */
    private ShopProfile mapRow(ResultSet rs) throws SQLException {
        ShopProfile p = new ShopProfile();
        p.setProfileId(rs.getInt("profile_id"));
        p.setUserId(rs.getInt("user_id"));
        p.setShopName(rs.getString("shop_name"));
        p.setShopDescription(rs.getString("shop_description"));
        p.setApprovalStatus(rs.getString("approval_status"));
        p.setRejectionReason(rs.getString("rejection_reason"));
        
        Timestamp approvedAtTs = rs.getTimestamp("approved_at");
        if (approvedAtTs != null) {
            p.setApprovedAt(approvedAtTs.toLocalDateTime());
        }
        p.setDeliveryAddress(rs.getString("delivery_address"));
        p.setRating(rs.getBigDecimal("rating"));
        
        Timestamp createdAtTs = rs.getTimestamp("created_at");
        if (createdAtTs != null) {
            p.setCreatedAt(createdAtTs.toLocalDateTime());
        }
        
        Timestamp updatedAtTs = rs.getTimestamp("updated_at");
        if (updatedAtTs != null) {
            p.setUpdatedAt(updatedAtTs.toLocalDateTime());
        }
        return p;
    }
}
