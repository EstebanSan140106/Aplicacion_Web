package com.esteban.ProyectoTarea;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.io.OutputStream;

@WebServlet ("/descarga/")
public class servlet extends HttpServlet {
    // Método que maneja las solicitudes GET
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Obtener el parámetro 'file' de la URL (indica si se solicita Excel o PDF)
        String fileType = request.getParameter("file");
        // Variables para almacenar el nombre del archivo y el tipo de contenido
        String fileName = "";
        String contentType = "";

        // Determinar el archivo y el tipo de contenido según el parámetro recibido
        if ("excel".equalsIgnoreCase(fileType)) {
            fileName = "Restaurantes_Franquicias_Ecuador.xlsx"; // Nombre del archivo Excel
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"; // MIME type para Excel
        } else if ("pdf".equalsIgnoreCase(fileType)) {
            fileName = "Restaurantes_Franquicias_Ecuador.pdf"; // Nombre del archivo PDF
            contentType = "application/pdf"; // MIME type para PDF
        } else {
            // Enviar error 400 si el parámetro es inválido
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tipo de archivo no válido");
            return;
        }

        // Obtener la ruta absoluta del archivo en la carpeta resources
        String filePath = getServletContext().getRealPath("/resources/" + fileName);
        // Crear un objeto File para el archivo solicitado
        File downloadFile = new File(filePath);

        // Verificar si el archivo existe en el servidor
        if (!downloadFile.exists()) {
            // Enviar error 404 si el archivo no se encuentra
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo no encontrado");
            return;
        }

        // Configurar las cabeceras HTTP de la respuesta
        response.setContentType(contentType); // Establecer el tipo de contenido
        response.setContentLength((int) downloadFile.length()); // Establecer el tamaño del archivo
        // Forzar la descarga del archivo con su nombre original
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        // Enviar el contenido del archivo al cliente
        try (FileInputStream inStream = new FileInputStream(downloadFile); // Flujo de entrada para leer el archivo
             OutputStream outStream = response.getOutputStream()) { // Flujo de salida para enviar al cliente
            byte[] buffer = new byte[4096]; // Buffer de 4KB para leer/escribir datos
            int bytesRead; // Número de bytes leídos en cada iteración
            // Leer el archivo en bloques y escribir en la respuesta
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        } // Los flujos se cierran automáticamente por el bloque try-with-resources
    }
}