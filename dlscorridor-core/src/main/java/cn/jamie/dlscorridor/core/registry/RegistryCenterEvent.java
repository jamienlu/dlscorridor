package cn.jamie.dlscorridor.core.registry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jamieLu
 * @create 2024-03-18
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistryCenterEvent {
    private List<String> nodes;
}
