package com.bank.bank_api.service;

import com.bank.bank_api.model.User;
import com.bank.bank_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class BankService {

    private UserRepository userRepository;

    public BankService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public ApiResponse getBalance(Long userId) {       //получение баланса
        if (userId == null || userId <= 0) {
            return new ApiResponse(-1, "Некоректный ID пользователя", null);

        }
        User user = userRepository.getBalance(userId);
        if (user == null) {
            return new ApiResponse(-1, "Пользователь с ID " + userId + " не найден", null);
        }
        return new ApiResponse(1, "Баланс успешно получен", user.getBalance());
    }

    public ApiResponse putMoney(Long userId, BigDecimal amount) {
        if (userId == null || userId <= 0) {
            return new ApiResponse(0, "Некоректный ID пользователя", null);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new ApiResponse(0, "Сумма должна быть положительной", null);
        }
        User user = userRepository.getBalance(userId);
        if (user == null) {
            return new ApiResponse(0, "Пользователь с ID " + userId + " не найден", null);
        }
        boolean success = userRepository.putMoney(userId, amount);
        if (success) {
            User updateUser = userRepository.getBalance(userId);
            return new ApiResponse(1, "Баланс успешно пополнен на " + amount + " руб.", updateUser.getBalance());
        }
        return new ApiResponse(0, "Ошибка при пополнении баланса", null);
    }

    public ApiResponse takeMoney(Long userId, BigDecimal amount) {
        if (userId == null || userId < 0) {
            return new ApiResponse(0, "Некоректный ID пользователя", null);
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new ApiResponse(0, "Сумма должна быть положительной", null);
        }
        int result = userRepository.takeMoney(userId, amount);
        switch (result) {
            case 1:
                User updateUser = userRepository.getBalance(userId);
                return new ApiResponse(1, "Снятие прошло успешно.Снято " + amount + " руб.", updateUser.getBalance());

            case 0:
                return new ApiResponse(0, "Недостаточно средств на счете", null);
            case -1:
                return new ApiResponse(0, "Пользователь с ID " + userId + " не найден", null);
            default:
                return new ApiResponse(0, "Неизвестная ошибка", null);
        }
    }

    public ApiResponse getOperationList(Long userId, String startDataStr, String endDataStr) {
        if (userId == null || userId < 0) {
            return new ApiResponse(0, "Некоректный ID пользователя", null);
        }

        User user = userRepository.getBalance(userId);
        if (user == null) {
            return new ApiResponse(0, "Пользователь с ID " + userId + " не найден", null);
        }

        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            if (startDataStr != null && !startDataStr.isEmpty()) {
                if (startDataStr.length() == 10) {
                    startDataStr += " 00:00:00";
                }
                startDate = LocalDateTime.parse(startDataStr, formatter);
            }

            if (endDataStr != null && !endDataStr.isEmpty()) {
                if (endDataStr.length() == 10) {
                    endDataStr += " 23:59:59";
                }
                endDate = LocalDateTime.parse(endDataStr, formatter);
            }
        } catch (Exception e) {
            return new ApiResponse(0, "Неверный формат датыю Используйте yyyy-MM-dd " +
                    "или yyyy-MM-dd HH:mm:ss", null);
        }

        List<Map<String, Object>> operations = userRepository.getOperationList(userId, startDate, endDate);
        return new ApiResponse(1, "Список операций выгружен успешно, найдено " + operations.size() +
                " операций", operations);


    }

    public ApiResponse transferMoney(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId == null || fromUserId <= 0) {
            return new ApiResponse(0, "Некоректный ID отправителя", null);
        }
        if(toUserId==null||toUserId<=0) {
            return new ApiResponse(0, "Некоректный ID получателя", null);
        }
        if(fromUserId.equals(toUserId)) {
            return new ApiResponse(0, "Невозможно отправить деньги самому себе", null);
        }
        if(amount==null||amount.compareTo(BigDecimal.ZERO)<=0) {
            return new ApiResponse(0, "Отправляемая сумма должна быть положительной", null);
        }

        int resultTransfer = userRepository.transferMoney(fromUserId,toUserId,amount);
        switch(resultTransfer) {
            case 1:
                User updateFromUser  = userRepository.getBalance(fromUserId);
                return new ApiResponse(1, "Перевод на сумму " + amount + " руб. успешно выполнен пользователя с ID "
                + toUserId, Map.of("fromBalance", updateFromUser.getBalance()));
            case 0:
                return new ApiResponse(0, "Недостаточно средств для перевода", null);
            case -1:
                return new ApiResponse(0, "ID отправителя не найден" + fromUserId, null);
            case -2:
                return new ApiResponse(0, "ID получателя не найден" + toUserId, null);
            default:
                return new ApiResponse(0, "Неизвестная ошибка", null);
        }
    }

    public static class ApiResponse {
        private final int status;
        private final String message;
        private final Object data;

        public ApiResponse(int status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}
