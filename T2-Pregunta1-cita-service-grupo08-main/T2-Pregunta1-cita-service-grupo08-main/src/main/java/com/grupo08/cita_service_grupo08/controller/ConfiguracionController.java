package com.grupo08.cita_service_grupo08.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Expone las propiedades centralizadas recibidas desde Config Server.
 * RefreshScope permite que los valores se actualicen después de POST /actuator/refresh.
 */
@RefreshScope
@RestController
@RequestMapping("/api/v1/citas")
public class ConfiguracionController {

    @Value("${salud.monto-copago-base:0.0}")
    private Double montoCopagoBase;

    @Value("${salud.descuento-eps:0.0}")
    private Double descuentoEps;

    @Value("${salud.mensaje-bienvenida:Configuración centralizada no disponible}")
    private String mensajeBienvenida;

    @Value("${salud.api-key-aseguradora:}")
    private String apiKeyAseguradora;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    /**
     * Devuelve los valores desencriptados que entrega Config Server para el perfil activo.
     */
    @GetMapping("/configuracion")
    public Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> configuracion = new LinkedHashMap<>();
        configuracion.put("monto-copago-base", montoCopagoBase);
        configuracion.put("descuento-eps", descuentoEps);
        configuracion.put("mensaje-bienvenida", mensajeBienvenida);
        configuracion.put("api-key-aseguradora", apiKeyAseguradora);
        configuracion.put("datasource.password", datasourcePassword);
        return configuracion;
    }
}
