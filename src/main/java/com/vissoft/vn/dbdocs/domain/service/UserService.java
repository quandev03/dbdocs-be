package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;

public interface UserService {
    /**
     * Lấy thông tin người dùng hiện tại
     * @return Thông tin người dùng hiện tại
     */
    UserDTO getCurrentUser();
} 