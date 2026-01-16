package com.example.factory.dto;

import java.util.List;

public record BatchResponse(
        int accepted,
        int deduped,
        int rejected,
        List<Rejection> rejections
) {
}
