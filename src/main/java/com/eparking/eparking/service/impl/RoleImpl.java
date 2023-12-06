package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.RoleMapper;
import com.eparking.eparking.domain.Role;
import com.eparking.eparking.domain.UserRole;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleImpl implements RoleService {
    private final RoleMapper roleMapper;
    @Override
    public List<UserRole> findRoleByPhoneNumber(String phoneNumber) {
        try {
            return roleMapper.findRoleByPhoneNumber(phoneNumber);
        }catch (Exception e){
            throw new ApiRequestException("Fail to find role by phone number" + e.getMessage());
        }
    }

    @Override
    public void insertUserRole(int roleID, int userID) {
        try {
            roleMapper.insertUserRole(roleID, userID);
        }catch (Exception e){
            throw new ApiRequestException("Fail to insert user role" + e.getMessage());
        }
    }

    @Override
    public boolean findRoleIDForUser(String phoneNumber) {
        try {
            return roleMapper.findRoleIDForUser(phoneNumber);
        }catch (Exception e){
            throw new ApiRequestException("Fail to find roleID for user" + e.getMessage());
        }
    }
}
