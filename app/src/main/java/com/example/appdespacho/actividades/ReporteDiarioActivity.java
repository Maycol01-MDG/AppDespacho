package com.example.appdespacho.actividades;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdespacho.R;
import com.example.appdespacho.database.CierreDiarioDAO;
import com.example.appdespacho.database.ConsumoDAO;
import com.example.appdespacho.database.TransferenciaCrcDAO;
import com.example.appdespacho.modelos.CierreDiario;
import com.example.appdespacho.modelos.Consumo;
import com.example.appdespacho.modelos.TransferenciaCrc;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReporteDiarioActivity extends AppCompatActivity {

    private EditText edtFecha;
    private Button btnCrearPDF;
    private ConsumoDAO consumoDAO;
    private TransferenciaCrcDAO transferenciaCrcDAO;
    private CierreDiarioDAO cierreDiarioDAO;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte_diario);

        // ✅ Inicializar vistas
        edtFecha = findViewById(R.id.edtFecha);
        btnCrearPDF = findViewById(R.id.btnCrearPdf);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // 📅 Selector de fecha
        edtFecha.setOnClickListener(v -> mostrarCalendario());

        // Inicializar DAOs
        consumoDAO = new ConsumoDAO(this);
        transferenciaCrcDAO = new TransferenciaCrcDAO(this);
        cierreDiarioDAO = new CierreDiarioDAO(this);

        // Botón crear PDF
        btnCrearPDF.setOnClickListener(v -> crearPDF());

        // Navegación inferior
        bottomNavigationView.setOnItemSelectedListener(this::handleBottomNavigation);
    }

    private void crearPDF() {
        try {
            // 📂 Ruta donde se guarda el PDF
            String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/Reportes";
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "reporte_diario.pdf");
            FileOutputStream outputStream = new FileOutputStream(file);

            // 📑 Documento PDF
            Document documento = new Document(PageSize.A4, 36, 36, 0, 0);
            PdfWriter.getInstance(documento, outputStream);
            documento.open();

            // =============================
            // Logos en esquinas superiores
            // =============================
            try {
                Bitmap bmpIzq = BitmapFactory.decodeResource(getResources(), R.drawable.logo_one);
                ByteArrayOutputStream streamIzq = new ByteArrayOutputStream();
                bmpIzq.compress(Bitmap.CompressFormat.PNG, 100, streamIzq);
                Image logoIzq = Image.getInstance(streamIzq.toByteArray());
                logoIzq.scaleToFit(100, 100);

                Bitmap bmpDer = BitmapFactory.decodeResource(getResources(), R.drawable.logoo_two);
                ByteArrayOutputStream streamDer = new ByteArrayOutputStream();
                bmpDer.compress(Bitmap.CompressFormat.PNG, 100, streamDer);
                Image logoDer = Image.getInstance(streamDer.toByteArray());
                logoDer.scaleToFit(100, 100);

                PdfPTable tablaLogos = new PdfPTable(2);
                tablaLogos.setWidthPercentage(100);
                tablaLogos.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

                PdfPCell celdaIzq = new PdfPCell(logoIzq);
                celdaIzq.setBorder(PdfPCell.NO_BORDER);
                celdaIzq.setHorizontalAlignment(Element.ALIGN_LEFT);

                PdfPCell celdaDer = new PdfPCell(logoDer);
                celdaDer.setBorder(PdfPCell.NO_BORDER);
                celdaDer.setHorizontalAlignment(Element.ALIGN_RIGHT);

                tablaLogos.addCell(celdaIzq);
                tablaLogos.addCell(celdaDer);

                documento.add(tablaLogos);

            } catch (Exception e) {
                e.printStackTrace();
            }

            // =============================
            // Encabezado principal
            // =============================
            Paragraph tituloPrincipal = new Paragraph(
                    "CONTROL DIARIO DE CONTÓMETROS DE SURTIDORES  -  GRIFO CORONA",
                    FontFactory.getFont("Arial", 16, Font.BOLD, BaseColor.BLACK)
            );
            tituloPrincipal.setAlignment(Element.ALIGN_CENTER);
            tituloPrincipal.setSpacingBefore(10);
            tituloPrincipal.setSpacingAfter(20);
            documento.add(tituloPrincipal);

            // =============================
            // Fecha seleccionada + Operador
            // =============================
            String fechaSeleccionada = edtFecha.getText().toString().trim();

            cierreDiarioDAO.open();
            List<CierreDiario> cierres = cierreDiarioDAO.obtenerTodos();
            cierreDiarioDAO.close();

            String operador = cierres.isEmpty() ? "" : cierres.get(0).getOperador();

            Paragraph infoInicial = new Paragraph(
                    "Fecha: " + fechaSeleccionada + "                Operador: " + operador,
                    FontFactory.getFont("Arial", 12, Font.NORMAL, BaseColor.BLACK)
            );
            infoInicial.setAlignment(Paragraph.ALIGN_LEFT);
            infoInicial.setSpacingAfter(20);
            documento.add(infoInicial);

            // 🎨 Fuentes para tablas
            Font fontHeader = FontFactory.getFont("Arial", 12, Font.BOLD, BaseColor.WHITE);
            Font fontCell = FontFactory.getFont("Arial", 11, Font.NORMAL, BaseColor.BLACK);

            // =============================
            // TABLA: Cierre Diario Contómetros
            // =============================
            Paragraph tituloCierre = new Paragraph("Cierre Diario - Contómetros",
                    FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK));
            tituloCierre.setAlignment(Paragraph.ALIGN_CENTER);
            tituloCierre.setSpacingBefore(15);
            tituloCierre.setSpacingAfter(10);
            documento.add(tituloCierre);

            PdfPTable tablaCierre = new PdfPTable(6);
            tablaCierre.setWidthPercentage(100);

            String[] headersCierre = {"Bomba", "Lectura Inicial", "Lectura Final", "Salida Vales", "Salida Contómetro", "Diferencia"};
            for (String header : headersCierre) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, fontHeader));
                headerCell.setBackgroundColor(new BaseColor(0, 121, 182));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaCierre.addCell(headerCell);
            }

            for (CierreDiario cd : cierres) {
                String bomba = cd.getBomba().trim();
                String nombreBomba;

                if (bomba.equalsIgnoreCase("1") || bomba.equalsIgnoreCase("B1") || bomba.equalsIgnoreCase("Bomba 1")) {
                    nombreBomba = "B1 DB5-S50";
                } else if (bomba.equalsIgnoreCase("2") || bomba.equalsIgnoreCase("B2") || bomba.equalsIgnoreCase("Bomba 2")) {
                    nombreBomba = "B2 DB5-S50";
                } else if (bomba.equalsIgnoreCase("3") || bomba.equalsIgnoreCase("B3") || bomba.equalsIgnoreCase("Bomba 3")) {
                    nombreBomba = "B3 GASOHOL 90";
                } else if (bomba.equalsIgnoreCase("4") || bomba.equalsIgnoreCase("B4") || bomba.equalsIgnoreCase("Bomba 4")) {
                    nombreBomba = "GARZA ";
                } else {
                    nombreBomba = bomba; // valor por defecto
                }

                tablaCierre.addCell(new Paragraph(nombreBomba, fontCell));
                tablaCierre.addCell(new Paragraph(String.valueOf(cd.getLecturaInicial()), fontCell));
                tablaCierre.addCell(new Paragraph(String.valueOf(cd.getLecturaFinal()), fontCell));
                tablaCierre.addCell(new Paragraph(String.valueOf(cd.getSalidaVales()), fontCell));
                tablaCierre.addCell(new Paragraph(String.valueOf(cd.getSalidaContometro()), fontCell));
                tablaCierre.addCell(new Paragraph(String.valueOf(cd.getDiferencia()), fontCell));
            }
            documento.add(tablaCierre);

            // =============================
            // TABLA: Consumo Interno
            // =============================
            consumoDAO.open();
            List<Consumo> consumos = consumoDAO.buscarConsumosPorFecha(fechaSeleccionada);
            consumoDAO.close();

            Paragraph tituloConsumo = new Paragraph("Consumo Interno",
                    FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK));
            tituloConsumo.setAlignment(Paragraph.ALIGN_CENTER);
            tituloConsumo.setSpacingBefore(15);
            tituloConsumo.setSpacingAfter(10);
            documento.add(tituloConsumo);

            PdfPTable tablaConsumo = new PdfPTable(4);
            tablaConsumo.setWidthPercentage(100);

            String[] headersConsumo = {"Código/Placa", "N° Vale", "Galones", "Bomba"};
            for (String header : headersConsumo) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, fontHeader));
                headerCell.setBackgroundColor(new BaseColor(0, 121, 182));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaConsumo.addCell(headerCell);
            }

            for (Consumo c : consumos) {
                if (c.getTipoConsumo() != null && c.getTipoConsumo().toUpperCase().contains("INTERNO")) {
                    tablaConsumo.addCell(new Paragraph(c.getPlaca(), fontCell));
                    tablaConsumo.addCell(new Paragraph(c.getNumeroVale(), fontCell));
                    tablaConsumo.addCell(new Paragraph(String.valueOf(c.getGalones()), fontCell));
                    tablaConsumo.addCell(new Paragraph(c.getBomba(), fontCell));
                }
            }
            documento.add(tablaConsumo);

            // =============================
            // TABLA: Vales Manuales
            // =============================
            Paragraph tituloVales = new Paragraph("Vales Manuales",
                    FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK));
            tituloVales.setAlignment(Paragraph.ALIGN_CENTER);
            tituloVales.setSpacingBefore(15);
            tituloVales.setSpacingAfter(10);
            documento.add(tituloVales);

            PdfPTable tablaVales = new PdfPTable(4);
            tablaVales.setWidthPercentage(100);

            for (String header : headersConsumo) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, fontHeader));
                headerCell.setBackgroundColor(new BaseColor(0, 121, 182));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaVales.addCell(headerCell);
            }

            for (Consumo c : consumos) {
                if (c.getTipoConsumo() != null &&
                        (c.getTipoConsumo().equalsIgnoreCase("Vale Manual")
                                || c.getTipoConsumo().equalsIgnoreCase("Vales Manuales")
                                || c.getTipoConsumo().toUpperCase().contains("VALE"))) {
                    tablaVales.addCell(new Paragraph(c.getPlaca(), fontCell));
                    tablaVales.addCell(new Paragraph(c.getNumeroVale(), fontCell));
                    tablaVales.addCell(new Paragraph(String.valueOf(c.getGalones()), fontCell));
                    tablaVales.addCell(new Paragraph(c.getBomba(), fontCell));
                }
            }
            documento.add(tablaVales);

            // =============================
            // TABLA: Transferencias CRC
            // =============================
            transferenciaCrcDAO.open();
            List<TransferenciaCrc> transferencias = transferenciaCrcDAO.buscarTransferenciasPorFecha(fechaSeleccionada);
            transferenciaCrcDAO.close();

            Paragraph tituloCRC = new Paragraph("Transferencias a CRC",
                    FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK));
            tituloCRC.setAlignment(Paragraph.ALIGN_CENTER);
            tituloCRC.setSpacingBefore(15);
            tituloCRC.setSpacingAfter(10);
            documento.add(tituloCRC);

            PdfPTable tablaCRC = new PdfPTable(3);
            tablaCRC.setWidthPercentage(100);

            String[] headersCRC = {"N° Vale", "Galones", "Camión CRC"};
            for (String header : headersCRC) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, fontHeader));
                headerCell.setBackgroundColor(new BaseColor(0, 121, 182));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaCRC.addCell(headerCell);
            }

            for (TransferenciaCrc t : transferencias) {
                tablaCRC.addCell(new Paragraph(t.getNumeroVale(), fontCell));
                tablaCRC.addCell(new Paragraph(String.valueOf(t.getGalones()), fontCell));
                tablaCRC.addCell(new Paragraph(t.getCamionCrc(), fontCell));
            }
            documento.add(tablaCRC);


            // =============================
// TABLA: Consumos por Bomba
// =============================
            Paragraph tituloTablaConsumos = new Paragraph("Ventas por Bomba",
                    FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK));
            tituloTablaConsumos.setAlignment(Element.ALIGN_CENTER);
            tituloTablaConsumos.setSpacingBefore(15);
            tituloTablaConsumos.setSpacingAfter(10);
            documento.add(tituloTablaConsumos);

            PdfPTable tablaConsumosBombas = new PdfPTable(4); // 4 columnas
            tablaConsumosBombas.setWidthPercentage(100);

// Encabezados
            String[] headersBombas = {"Venta", "Bomba 1", "Bomba 2", "Bomba 3"};
            for (String header : headersBombas) {
                PdfPCell headerCell = new PdfPCell(new Paragraph(header, fontHeader));
                headerCell.setBackgroundColor(new BaseColor(0, 121, 182));
                headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                tablaConsumosBombas.addCell(headerCell);
            }

// Variables para acumular valores
            double valesB1 = 0, valesB2 = 0, valesB3 = 0, v=0;
            double internosB1 = 0, internosB2 = 0, internosB3 = 0;
            double ST1 = 0, ST2 = 0, ST3 = 0;

// Recorremos consumos para acumular por tipo
            for (Consumo c : consumos) {
                String bomba = c.getBomba().trim();
                double gal = c.getGalones();

                if (c.getTipoConsumo() != null) {
                    if (c.getTipoConsumo().toUpperCase().contains("VALE")) {
                        if (bomba.equalsIgnoreCase("1") || bomba.equalsIgnoreCase("B1") || bomba.equalsIgnoreCase("Bomba 1"))
                            valesB1 += gal;
                        else if (bomba.equalsIgnoreCase("2") || bomba.equalsIgnoreCase("B2") || bomba.equalsIgnoreCase("Bomba 2"))
                            valesB2 += gal;
                        else if (bomba.equalsIgnoreCase("3") || bomba.equalsIgnoreCase("B3") || bomba.equalsIgnoreCase("Bomba 3"))
                            valesB3 += gal;
                    } else if (c.getTipoConsumo().toUpperCase().contains("INTERNO")) {
                        if (bomba.equalsIgnoreCase("1") || bomba.equalsIgnoreCase("B1") || bomba.equalsIgnoreCase("Bomba 1"))
                            internosB1 += gal;
                        else if (bomba.equalsIgnoreCase("2") || bomba.equalsIgnoreCase("B2") || bomba.equalsIgnoreCase("Bomba 2"))
                            internosB2 += gal;
                        else if (bomba.equalsIgnoreCase("3") || bomba.equalsIgnoreCase("B3") || bomba.equalsIgnoreCase("Bomba 3"))
                            internosB3 += gal;
                    }
                }
            }
            // =============================
// Sub Total - Restando Salida Vales del Cierre Diario
// =============================
            double salidaValesB1 = 0, salidaValesB2 = 0, salidaValesB3 = 0;

// Buscar en cierres el valor de salidaVales por bomba
            for (CierreDiario cd : cierres) {
                String bomba = cd.getBomba().trim();
                if (bomba.equalsIgnoreCase("1") || bomba.equalsIgnoreCase("B1") || bomba.equalsIgnoreCase("Bomba 1")) {
                    salidaValesB1 = cd.getSalidaVales();
                } else if (bomba.equalsIgnoreCase("2") || bomba.equalsIgnoreCase("B2") || bomba.equalsIgnoreCase("Bomba 2")) {
                    salidaValesB2 = cd.getSalidaVales();
                } else if (bomba.equalsIgnoreCase("3") || bomba.equalsIgnoreCase("B3") || bomba.equalsIgnoreCase("Bomba 3")) {
                    salidaValesB3 = cd.getSalidaVales();
                }
            }
            //Generación de sub totales
            ST1=valesB1 + internosB1;
            ST2=valesB2 + internosB2;
            ST3=valesB3 + internosB3;
            v= valesB1 + internosB1;

// Fila 1: Total por Vales Manuales
            tablaConsumosBombas.addCell(new Paragraph("Venta Manual", fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(valesB1), fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(valesB2), fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(valesB3), fontCell));

// Fila 2: Total por Consumo Interno
            tablaConsumosBombas.addCell(new Paragraph("Venta automática", fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(salidaValesB1-valesB1-internosB1), fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(salidaValesB2-valesB2-internosB2), fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(salidaValesB3-valesB3-internosB3), fontCell));

// Fila 3: Sub Total
            tablaConsumosBombas.addCell(new Paragraph("Sub Total", fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(ST1), fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(ST2), fontCell));
            tablaConsumosBombas.addCell(new Paragraph(String.valueOf(ST3), fontCell));

            documento.add(tablaConsumosBombas);


// =============================
// TABLA: Resumen General
// =============================
            Paragraph tituloResumen = new Paragraph("Resumen General",
                    FontFactory.getFont("Arial", 14, Font.BOLD, BaseColor.BLACK));
            tituloResumen.setAlignment(Paragraph.ALIGN_CENTER);
            tituloResumen.setSpacingBefore(15);
            tituloResumen.setSpacingAfter(10);
            documento.add(tituloResumen);

            PdfPTable tablaResumen = new PdfPTable(2);
            tablaResumen.setWidthPercentage(60);
            tablaResumen.setHorizontalAlignment(Element.ALIGN_CENTER);

// Encabezados
            PdfPCell headerBomba = new PdfPCell(new Paragraph("Bomba", fontHeader));
            headerBomba.setBackgroundColor(new BaseColor(0, 121, 182));
            headerBomba.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaResumen.addCell(headerBomba);

            PdfPCell headerGalones = new PdfPCell(new Paragraph("Total", fontHeader));
            headerGalones.setBackgroundColor(new BaseColor(0, 121, 182));
            headerGalones.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaResumen.addCell(headerGalones);

// Inicializar acumuladores
            double  galonesB4 = 0;

// Contar galones de consumos (bombas 1,2,3)


// Contar galones de transferencias CRC (solo bomba 4)
            for (TransferenciaCrc t : transferencias) {
                galonesB4 += t.getGalones();
            }

// Agregar filas a la tabla
            tablaResumen.addCell(new Paragraph("TOTAL DB5-S50", fontCell));
            tablaResumen.addCell(new Paragraph(String.valueOf(ST1 +ST2), fontCell));

            tablaResumen.addCell(new Paragraph("TOTAL GASOHOL", fontCell));
            tablaResumen.addCell(new Paragraph(String.valueOf(ST3), fontCell));

            tablaResumen.addCell(new Paragraph("TOTAL GARZA", fontCell));
            tablaResumen.addCell(new Paragraph(String.valueOf(galonesB4), fontCell));

/* Gran Total VER SI REQUIERE O NO
            double totalGeneral = ST1+ST2+ST3 + galonesB4;
            PdfPCell totalCell = new PdfPCell(new Paragraph("TOTAL GENERAL", fontHeader));
            totalCell.setBackgroundColor(new BaseColor(200, 0, 0)); // rojo para resaltar
            totalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaResumen.addCell(totalCell);

            PdfPCell totalGalCell = new PdfPCell(new Paragraph(String.valueOf(totalGeneral), fontHeader));
            totalGalCell.setBackgroundColor(new BaseColor(200, 0, 0));
            totalGalCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tablaResumen.addCell(totalGalCell); */

            documento.add(tablaResumen);


            // =============================
            // Cerrar documento
            // =============================
            documento.close();
            Toast.makeText(this, "✅ PDF creado en: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "❌ Error al crear PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // 🔹 Método de navegación inferior
    private boolean handleBottomNavigation(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        if (id == R.id.Registro_Consumo) {
            startActivity(new Intent(this, RegistroConsumoActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        if (id == R.id.Registro_transferenciaCRC) {
            startActivity(new Intent(this, TransferenciaCrcActivity.class));
            overridePendingTransition(0, 0);
            return true;
        }

        if (id == R.id.logut) {
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private void mostrarCalendario() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                    edtFecha.setText(fecha);
                },
                anio, mes, dia
        );
        datePicker.show();
    }
}
