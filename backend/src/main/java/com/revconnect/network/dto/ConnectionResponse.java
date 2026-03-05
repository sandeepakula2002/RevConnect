package com.revconnect.network.dto;

import com.revconnect.network.model.Connection;
import com.revconnect.network.model.ConnectionStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConnectionResponse {

    private Long id;

    private Long requesterId;
    private String requesterUsername;
    private String requesterFullName;

    private Long addresseeId;
    private String addresseeUsername;
    private String addresseeFullName;

    private ConnectionStatus status;

    public static ConnectionResponse from(Connection c) {

        return ConnectionResponse.builder()
                .id(c.getId())

                .requesterId(c.getRequester().getId())
                .requesterUsername(c.getRequester().getUsername())
                .requesterFullName(c.getRequester().getFullName())

                .addresseeId(c.getAddressee().getId())
                .addresseeUsername(c.getAddressee().getUsername())
                .addresseeFullName(c.getAddressee().getFullName())

                .status(c.getStatus())
                .build();
    }
}