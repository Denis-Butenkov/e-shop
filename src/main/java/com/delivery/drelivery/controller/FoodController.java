package com.delivery.drelivery.controller;

import com.delivery.drelivery.dto.FoodRequest;
import com.delivery.drelivery.dto.FoodResponse;
import com.delivery.drelivery.service.FoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse addFood(@RequestPart("food") FoodRequest request,
                                @RequestPart("file") MultipartFile file) {
        log.info("Received request for adding a food: {}", request);
        return foodService.addFood(request, file);
    }

    @GetMapping
    public List<FoodResponse> readFoods(){
        log.info("Received request for reading all foods");
        return foodService.readFoods();
    }

    @GetMapping("/{id}")
    public FoodResponse readFood(@PathVariable String id){
        log.info("Received request for reading a food with id: {}", id);
        return foodService.readFood(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable String id){
        log.info("Received request for deleting a food with id: {}", id);
        foodService.deleteFood(id);
    }
}
