package com.delivery.drelivery.service;

import com.delivery.drelivery.dto.FoodRequest;
import com.delivery.drelivery.dto.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    FoodResponse addFood(FoodRequest request, MultipartFile file);

    List<FoodResponse>readFoods();

    FoodResponse readFood(String id);

    void deleteFood(String id);

}
