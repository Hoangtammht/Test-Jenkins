package com.eparking.eparking.dao;

import com.eparking.eparking.domain.Role;
import com.eparking.eparking.domain.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    List<UserRole> findRoleByPhoneNumber(String phoneNumber);
    void insertUserRole(int roleID, int userID);
    boolean findRoleIDForUser(String phoneNumber);
    List<Role> findRoleForUser(int userID);
}
