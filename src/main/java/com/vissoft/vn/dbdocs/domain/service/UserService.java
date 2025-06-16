package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;

public interface UserService {
    /**
     * Lấy thông tin người dùng hiện tại
     * @return Thông tin người dùng hiện tại
     */
    UserDTO getCurrentUser();
    
    /**
     * Lấy thông tin người dùng theo ID
     * @param userId ID của người dùng
     * @return Thông tin người dùng
     */
    UserDTO getUserById(String userId);
} 