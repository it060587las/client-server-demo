package com.dataart.edu.message.dto.response;

import com.dataart.edu.message.dto.BaseMessageDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Answer from server to client.
 *
 * @see BaseMessageDto
 *
 * @author alitvinov
 * @version 1.0.0
 * @since 2017-09-07
 */
@Data
@NoArgsConstructor
public class ServerResponseDto extends BaseMessageDto {

    private List<?> resultData;

    private boolean success = true;

    private String error;
}
