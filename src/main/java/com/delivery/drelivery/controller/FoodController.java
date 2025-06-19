package com.delivery.drelivery.controller;

import com.delivery.drelivery.dto.FoodRequest;
import com.delivery.drelivery.dto.FoodResponse;
import com.delivery.drelivery.service.FoodService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse addFood(@RequestPart("food") String foodJson,
                                @RequestPart("file") MultipartFile file) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FoodRequest request = mapper.readValue(foodJson, FoodRequest.class);
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
