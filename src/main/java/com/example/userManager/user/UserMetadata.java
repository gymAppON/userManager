package com.example.userManager.user;

import com.example.userManager.enums.LanguageEnum;
import com.example.userManager.enums.WeightUnitEnum;

public record UserMetadata(WeightUnitEnum weightUnit, LanguageEnum language, String timezone) {
}
