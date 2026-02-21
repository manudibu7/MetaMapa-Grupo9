package com.metamapa.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
@Slf4j
@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String subirArchivo(MultipartFile archivo) {
        try {
            // Cloudinary detecta automáticamente si es imagen, video o raw (pdf)
            // 'auto' permite que Cloudinary decida el tipo de recurso
            log.info("Subiendo archivo a Cloudinary: nombre={}", archivo.getOriginalFilename());
            Map uploadResult = cloudinary.uploader().upload(archivo.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));

            // Obtenemos la URL segura (https)
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            log.error("Error subiendo archivo a Cloudinary: nombre={}, error={}", archivo.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Error subiendo a Cloudinary", e);
        }
    }

    // Opcional: Método para borrar si el usuario elimina el hecho
    public void eliminarArchivo(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}