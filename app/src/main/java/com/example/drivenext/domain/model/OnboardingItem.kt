package com.example.drivenext.domain.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Модель данных для страницы онбординга
 *
 * @param imageRes идентификатор ресурса изображения
 * @param titleRes идентификатор ресурса для заголовка
 * @param descriptionRes идентификатор ресурса для описания
 */
data class OnboardingItem(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
)