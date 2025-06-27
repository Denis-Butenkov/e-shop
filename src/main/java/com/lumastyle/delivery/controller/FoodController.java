package com.lumastyle.delivery.controller;

import com.lumastyle.delivery.dto.food.FoodRequest;
import com.lumastyle.delivery.dto.food.FoodResponse;
import com.lumastyle.delivery.service.FoodService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {

    private static final String NAME_VALIDATION_PATTERN = "^[A-Za-z0-9\\-]+$";
    private final FoodService foodService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public FoodResponse addFood(@Valid @RequestPart("food") FoodRequest request,
                                @RequestPart("file") MultipartFile file) {
        log.info("Received request to add a new food: {}", request);
        return foodService.addFood(request, file);
    }

    @GetMapping
    public List<FoodResponse> readFoods() {
        log.info("Received request for reading all foods");
        return foodService.readFoods();
    }

    @GetMapping("/{id}")
    public FoodResponse readFood(@PathVariable
                                 @Pattern(regexp = NAME_VALIDATION_PATTERN, message = "Invalid food id") String id) {
        log.info("Received request for reading a food with id: {}", id);
        return foodService.readFood(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFood(@PathVariable @Pattern(regexp = NAME_VALIDATION_PATTERN,
            message = "Invalid food id") String id) {
        log.info("Received request for deleting a food with id: {}", id);
        foodService.deleteFood(id);
    }
}
