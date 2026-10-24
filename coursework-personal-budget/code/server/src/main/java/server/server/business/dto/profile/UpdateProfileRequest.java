package server.server.business.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import server.server.common.annotation.ValidCurrency;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @NotBlank(message = "Имя обязательно для заполнения")
        @Size(max = 100, message = "Имя не должно превышать 100 символов")
        String firstName,

        @NotBlank(message = "Фамилия обязательна для заполнения")
        @Size(max = 100, message = "Фамилия не должна превышать 100 символов")
        String lastName,

        @Pattern(regexp = "^(\\+375\\d{9})?$", message = "Формат номера: +375XXXXXXXXX (9 цифр)")
        String phone,

        @Past(message = "Дата рождения должна быть в прошлом")
        LocalDate birthDate,

        @ValidCurrency
        String defaultCurrency
) {}
