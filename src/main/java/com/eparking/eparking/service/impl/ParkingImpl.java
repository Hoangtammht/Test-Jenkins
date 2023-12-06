package com.eparking.eparking.service.impl;

import com.eparking.eparking.dao.ParkingMapper;
import com.eparking.eparking.domain.Parking;
import com.eparking.eparking.domain.ParkingDate;
import com.eparking.eparking.domain.ParkingSpecialDate;
import com.eparking.eparking.domain.SpecialDate;
import com.eparking.eparking.domain.response.ResponseParking;
import com.eparking.eparking.exception.ApiRequestException;
import com.eparking.eparking.service.interf.ParkingService;
import com.eparking.eparking.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
//import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParkingImpl implements ParkingService {

    private final ParkingMapper parkingMapper;

    private final UserService userService;

//    private final CacheManager cacheManager;

    @Override
    @Transactional
    public ResponseParking createParking(Parking parking) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            parkingMapper.createParking(parking, userID);
//            clearNearbyParkingCache();
            return parkingMapper.findParkingByParkingID(parking.getParkingID());
        } catch (Exception e) {
            throw new ApiRequestException("Failed to create parking: " + e.getMessage());
        }
    }

    @Override
    public SpecialDate createSpecialDate(SpecialDate specialDate) {
        try {
            parkingMapper.createSpecialDate(specialDate);
            return specialDate;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to create parking: " + e.getMessage());
        }
    }

    @Override
    public List<ParkingDate> addDatesForParking(List<ParkingDate> parkingDates) {
        try {
            parkingMapper.addDatesForParking(parkingDates);
            return parkingDates;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to add date for Parking : " + e.getMessage());
        }
    }

    @Override
    public List<ParkingSpecialDate> addSpecialDatesForParking(List<ParkingSpecialDate> parkingSpecialDates) {
        try {
            parkingMapper.addSpecialDatesForParking(parkingSpecialDates);
            return parkingSpecialDates;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to add date for Special Parking : " + e.getMessage());
        }
    }

    @Override
    public ResponseParking getParkingDetail(int parkingID) {
        try {
            ResponseParking parkingDetail = parkingMapper.findParkingByParkingID(parkingID);
            return parkingDetail;
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get parking detail: " + e.getMessage());
        }
    }

    @Override
    public Page<ResponseParking> getListParking(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            long offset = pageable.getOffset();
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            int userID = userService.findUserByPhoneNumber(authentication.getName()).getUserID();
            List<ResponseParking> parkingList = parkingMapper.getListParking(size, offset,userID);
            long totalCount = parkingMapper.getNumberOfParkings();
            return new PageImpl<>(parkingList, pageable, totalCount);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get all Parking: " + e.getMessage());
        }
    }

    @Override
    @Cacheable(cacheNames = "Parking", key = "T(java.lang.String).valueOf(#latitude).concat(':').concat(T(java.lang.String).valueOf(#longitude)).concat(':').concat(#page).concat(':').concat(#size).concat(':').concat(#sortBy).concat(':').concat(#radius)")
    public Page<ResponseParking> searchNearbyParking(double latitude, double longitude, int page, int size, String sortBy, double radius) {
        try {
            Sort sort = null;
            if (sortBy != null) {
                switch (sortBy.toLowerCase()) {
                    case "cheapest":
                        sort = Sort.by("pricing");
                        break;
                    case "nearest":
                        sort = Sort.by("distance");
                        break;
                    case "cheapest_nearest":
                        sort = Sort.by("pricing", "distance");
                        break;
                    default:
                        throw new ApiRequestException("Invalid sorting option.");
                }
            }
            Pageable pageable = PageRequest.of(page, size, sort);
            long offset = pageable.getOffset();
            List<ResponseParking> parkingList = parkingMapper.searchNearbyParking(latitude, longitude, radius, size, offset, sortBy);
            long totalCount = parkingMapper.getNumberOfParkings();
            return new PageImpl<>(parkingList, pageable, totalCount);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to get list Parking Nearby: " + e.getMessage());
        }
    }


    @Override
    @Transactional
//    @CacheEvict(cacheNames = "Parking", key = "#parkingID")
    public ResponseParking updatePricingByParkingID(int parkingID, int pricing) {
        try {
//            clearNearbyParkingCache();
            parkingMapper.updatePricingByParkingID(parkingID, pricing);
            return parkingMapper.findParkingByParkingID(parkingID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to update pricing by parkingID: " + e.getMessage());
        }
    }

    @Override
    @Transactional
//    @CacheEvict(cacheNames = "Parking", key = "#parkingID")
    public ResponseParking updateSlotByParkingID(int parkingID, int park) {
        try {
//            clearNearbyParkingCache();
            parkingMapper.updateParkForParking(parkingID, park);
            return parkingMapper.findParkingByParkingID(parkingID);
        } catch (Exception e) {
            throw new ApiRequestException("Failed to update slot by parkingID: " + e.getMessage());
        }
    }

//    private void clearNearbyParkingCache() {
//        Cache parkingCache = cacheManager.getCache("Parking");
//        parkingCache.clear();
//    }
}
