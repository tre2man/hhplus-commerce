package kr.hhplus.be.server.domain.dataplatform.command;

import java.time.LocalDateTime;

public record GetTopNCommand(
    LocalDateTime date,
    int day
) {
}
