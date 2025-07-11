package nh.recipify.domain.api.admin;

import nh.recipify.domain.model.MealType;

record MealTypeDto(String id, String name) {

    static MealTypeDto of(MealType mealType) {
        return new MealTypeDto(mealType.getId().toString(),
            mealType.getName());
    }

}
