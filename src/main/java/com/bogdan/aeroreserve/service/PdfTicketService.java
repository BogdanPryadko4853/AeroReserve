package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.BookingEntity;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfTicketService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public byte[] generateTicketPdf(BookingEntity booking) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);

            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            addHeader(document, boldFont, booking);

            addPassengerFlightInfo(document, boldFont, normalFont, booking);

            addRouteTimingInfo(document, boldFont, normalFont, booking);

            addSeatBookingInfo(document, boldFont, normalFont, booking);

            addFooter(document, normalFont);

            document.close();
            return baos.toByteArray();

        } catch (IOException e) {
            log.error("Error generating PDF ticket", e);
            throw new RuntimeException("Failed to generate ticket PDF", e);
        }
    }

    private void addHeader(Document document, PdfFont boldFont, BookingEntity booking) {
        Paragraph title = new Paragraph("E-TICKET - BOARDING PASS")
                .setFont(boldFont)
                .setFontSize(20)
                .setFontColor(ColorConstants.BLUE)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        Table headerTable = new Table(2);
        headerTable.setWidth(UnitValue.createPercentValue(100));

        headerTable.addCell(new Cell().add(new Paragraph("AeroReserve Airlines")
                .setFont(boldFont)
                .setFontSize(16)));
        headerTable.addCell(new Cell().add(new Paragraph("Booking: " + booking.getBookingNumber())
                .setFont(boldFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.RIGHT)));

        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    private void addPassengerFlightInfo(Document document, PdfFont boldFont, PdfFont normalFont, BookingEntity booking) {
        Table infoTable = new Table(new float[]{1, 1});
        infoTable.setWidth(UnitValue.createPercentValue(100));
        infoTable.setMarginBottom(15);

        infoTable.addCell(createCell("PASSENGER INFORMATION", boldFont, true));
        infoTable.addCell(createCell("FLIGHT INFORMATION", boldFont, true));

        infoTable.addCell(createCell("Name: " + booking.getPassengerName(), normalFont, false));
        infoTable.addCell(createCell("Flight: " + booking.getFlight().getFlightNumber(), normalFont, false));

        infoTable.addCell(createCell("Booking Ref: " + booking.getBookingNumber(), normalFont, false));
        infoTable.addCell(createCell("Aircraft: " + booking.getFlight().getAircraft().getModel(), normalFont, false));

        infoTable.addCell(createCell("Status: " + booking.getStatus(), normalFont, false));
        infoTable.addCell(createCell("Airline: " +
                (booking.getFlight().getAirline() != null ?
                        booking.getFlight().getAirline().getName() : "AeroReserve"), normalFont, false));

        document.add(infoTable);
    }

    private void addRouteTimingInfo(Document document, PdfFont boldFont, PdfFont normalFont, BookingEntity booking) {
        Table routeTable = new Table(new float[]{2, 1, 2});
        routeTable.setWidth(UnitValue.createPercentValue(100));
        routeTable.setMarginBottom(15);

        routeTable.addCell(createCell("DEPARTURE", boldFont, true));
        routeTable.addCell(createCell("", boldFont, true));
        routeTable.addCell(createCell("ARRIVAL", boldFont, true));

        routeTable.addCell(createCell(booking.getFlight().getDepartureCity(), boldFont, false)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER));
        routeTable.addCell(createCell("➝", boldFont, false)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER));
        routeTable.addCell(createCell(booking.getFlight().getArrivalCity(), boldFont, false)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER));

        routeTable.addCell(createCell(
                DATE_FORMATTER.format(booking.getFlight().getDepartureTime()) + "\n" +
                        TIME_FORMATTER.format(booking.getFlight().getDepartureTime()),
                normalFont, false).setTextAlignment(TextAlignment.CENTER));

        routeTable.addCell(createCell("Duration\n" +
                        TIME_FORMATTER.format(booking.getFlight().getArrivalTime()),
                normalFont, false).setTextAlignment(TextAlignment.CENTER));

        routeTable.addCell(createCell(
                DATE_FORMATTER.format(booking.getFlight().getArrivalTime()) + "\n" +
                        TIME_FORMATTER.format(booking.getFlight().getArrivalTime()),
                normalFont, false).setTextAlignment(TextAlignment.CENTER));

        document.add(routeTable);
    }

    private void addSeatBookingInfo(Document document, PdfFont boldFont, PdfFont normalFont, BookingEntity booking) {
        Table detailsTable = new Table(new float[]{1, 1});
        detailsTable.setWidth(UnitValue.createPercentValue(100));
        detailsTable.setMarginBottom(15);

        detailsTable.addCell(createCell("SEAT INFORMATION", boldFont, true));
        detailsTable.addCell(createCell("BOOKING DETAILS", boldFont, true));

        detailsTable.addCell(createCell(
                "Seat: " + booking.getSeat().getSeatNumber() + "\n" +
                        "Class: " + booking.getSeat().getSeatClass() + "\n" +
                        "Aircraft: " + booking.getFlight().getAircraft().getManufacturer() + " " +
                        booking.getFlight().getAircraft().getModel(),
                normalFont, false));

        detailsTable.addCell(createCell(
                "Booked: " + DATE_FORMATTER.format(booking.getBookingDate()) + "\n" +
                        "Amount: $" + booking.getTotalPrice() + "\n" +
                        "Status: " + booking.getStatus(),
                normalFont, false));

        document.add(detailsTable);
    }

    private void addFooter(Document document, PdfFont normalFont) {
        document.add(new Paragraph("\n\n"));

        Paragraph footer = new Paragraph(
                "Thank you for choosing AeroReserve!\n" +
                        "• Please arrive at the airport at least 2 hours before departure\n" +
                        "• Have your ID/passport ready for check-in\n" +
                        "• Boarding starts 45 minutes before departure\n" +
                        "• For assistance, contact: support@aeroreserve.com"
        )
                .setFont(normalFont)
                .setFontSize(10)
                .setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(footer);

        Paragraph qrNote = new Paragraph(
                "[QR Code for mobile boarding would be here]"
        )
                .setFont(normalFont)
                .setFontSize(8)
                .setFontColor(ColorConstants.LIGHT_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(10);

        document.add(qrNote);
    }

    private Cell createCell(String text, PdfFont font, boolean isHeader) {
        Cell cell = new Cell();
        Paragraph paragraph = new Paragraph(text).setFont(font);

        if (isHeader) {
            paragraph.setBold();
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        }

        cell.add(paragraph);
        cell.setPadding(5);
        return cell;
    }
}