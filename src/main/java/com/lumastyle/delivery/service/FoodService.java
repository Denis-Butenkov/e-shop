package com.lumastyle.delivery.service;

import com.lumastyle.delivery.dto.FoodRequest;
import com.lumastyle.delivery.dto.FoodResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FoodService {

    FoodResponse addFood(FoodRequest request, MultipartFile file);

    List<FoodResponse>readFoods();

    FoodResponse readFood(String id);

    void deleteFood(String id);

}
