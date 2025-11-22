package com.example.userManager.domain.user;

import com.example.userManager.shared.enums.LanguageEnum;
import com.example.userManager.shared.enums.WeightUnitEnum;

public record UserMetadata(WeightUnitEnum weightUnit, LanguageEnum language, String timezone) {
}
