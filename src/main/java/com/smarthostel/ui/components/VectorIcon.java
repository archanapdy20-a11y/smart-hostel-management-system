package com.smarthostel.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

// Renders crisp geometric vector icons using Java2D Graphics
public class VectorIcon implements Icon {

    public enum Type {
        DASHBOARD, STUDENTS, ROOMS, ALLOCATION, FEES, COMPLAINTS,
        FOOD_MENU, FOOD_FEEDBACK, USERS, REPORTS, PROFILE, LOGOUT,
        SEARCH, REFRESH, ADD, EDIT, DELETE, EXPORT_CSV, PRINT_PDF,
        CHECK, LOCK, INFO, STAR, HOSTEL, EYE, EYE_OFF, CALENDAR,
        BED, DOOR, USER_PLUS, KEY, BELL, TREND_UP, FILTER, SHIELD_CHECK,
        BUILDING, WARNING, CHEVRON_LEFT, CHEVRON_RIGHT, MENU
    }

    private final Type type;
    private final int size;
    private Color color;

    public VectorIcon(Type type, int size, Color color) {
        this.type = type;
        this.size = size;
        this.color = color;
    }

    public VectorIcon(Type type, int size) {
        this(type, size, Color.BLACK);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        g2.translate(x, y);
        double scale = (double) size / 24.0;
        g2.scale(scale, scale);

        g2.setColor(color != null ? color : (c != null ? c.getForeground() : Color.BLACK));
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        drawGlyph(g2);
        g2.dispose();
    }

    private void drawGlyph(Graphics2D g2) {
        switch (type) {
            case DASHBOARD:
                g2.fill(new RoundRectangle2D.Float(3, 3, 7.5f, 7.5f, 2, 2));
                g2.fill(new RoundRectangle2D.Float(13.5f, 3, 7.5f, 7.5f, 2, 2));
                g2.fill(new RoundRectangle2D.Float(3, 13.5f, 7.5f, 7.5f, 2, 2));
                g2.fill(new RoundRectangle2D.Float(13.5f, 13.5f, 7.5f, 7.5f, 2, 2));
                break;

            case STUDENTS:
                // Student graduation cap and persona
                Path2D cap = new Path2D.Float();
                cap.moveTo(12, 4);
                cap.lineTo(22, 8.5f);
                cap.lineTo(12, 13);
                cap.lineTo(2, 8.5f);
                cap.closePath();
                g2.fill(cap);
                g2.draw(new Arc2D.Float(6, 9.5f, 12, 7, 0, -180, Arc2D.OPEN));
                g2.draw(new Line2D.Float(21, 9, 21, 16));
                g2.fill(new Ellipse2D.Float(20, 15, 2, 2));
                g2.draw(new Arc2D.Float(4, 15, 16, 12, 0, 180, Arc2D.OPEN));
                break;

            case USER_PLUS:
                // Student with addition badge
                g2.draw(new Ellipse2D.Float(5, 4, 7, 7));
                g2.draw(new Arc2D.Float(2, 13, 13, 10, 0, 180, Arc2D.OPEN));
                g2.draw(new Line2D.Float(19, 9, 19, 17));
                g2.draw(new Line2D.Float(15, 13, 23, 13));
                break;

            case ROOMS:
            case BED:
                // Hostel bunk bed outline
                g2.draw(new Line2D.Float(3, 4, 3, 20));
                g2.draw(new Line2D.Float(21, 4, 21, 20));
                g2.draw(new Line2D.Float(3, 14, 21, 14));
                g2.draw(new Line2D.Float(3, 8, 21, 8));
                g2.fill(new RoundRectangle2D.Float(5, 5.5f, 4.5f, 2.5f, 1, 1));
                g2.fill(new RoundRectangle2D.Float(5, 11.5f, 4.5f, 2.5f, 1, 1));
                g2.draw(new Line2D.Float(17, 8, 17, 20));
                break;

            case ALLOCATION:
            case KEY:
                // Room key icon
                g2.draw(new Ellipse2D.Float(3, 3, 9, 9));
                g2.fill(new Ellipse2D.Float(6.5f, 6.5f, 2, 2));
                g2.draw(new Line2D.Float(10.5f, 10.5f, 20, 20));
                g2.draw(new Line2D.Float(17, 17, 19, 15));
                g2.draw(new Line2D.Float(19, 19, 21, 17));
                break;

            case DOOR:
                // Hostel room door
                g2.draw(new RoundRectangle2D.Float(5, 3, 14, 18, 2, 2));
                g2.fill(new Ellipse2D.Float(15, 12, 2, 2));
                g2.draw(new Line2D.Float(3, 21, 21, 21));
                break;

            case FEES:
                // Credit card & ledger
                g2.draw(new RoundRectangle2D.Float(2, 5, 20, 14, 3, 3));
                g2.fill(new Rectangle2D.Float(2, 8.5f, 20, 3));
                g2.fill(new RoundRectangle2D.Float(5, 13.5f, 4, 2.5f, 1, 1));
                g2.draw(new Line2D.Float(13, 15, 18, 15));
                break;

            case COMPLAINTS:
                // Alert warning shield
                Path2D shield = new Path2D.Float();
                shield.moveTo(12, 3);
                shield.lineTo(21, 6.5f);
                shield.lineTo(21, 12);
                shield.curveTo(21, 17, 17, 20.5f, 12, 22);
                shield.curveTo(7, 20.5f, 3, 17, 3, 12);
                shield.lineTo(3, 6.5f);
                shield.closePath();
                g2.draw(shield);
                g2.draw(new Line2D.Float(12, 8, 12, 13));
                g2.fill(new Ellipse2D.Float(11, 15.5f, 2, 2));
                break;

            case SHIELD_CHECK:
                Path2D sCheck = new Path2D.Float();
                sCheck.moveTo(12, 3);
                sCheck.lineTo(21, 6.5f);
                sCheck.lineTo(21, 12);
                sCheck.curveTo(21, 17, 17, 20.5f, 12, 22);
                sCheck.curveTo(7, 20.5f, 3, 17, 3, 12);
                sCheck.lineTo(3, 6.5f);
                sCheck.closePath();
                g2.draw(sCheck);
                Path2D ck = new Path2D.Float();
                ck.moveTo(8, 12);
                ck.lineTo(11, 15);
                ck.lineTo(16, 9);
                g2.draw(ck);
                break;

            case FOOD_MENU:
                // Fork & Knife
                g2.draw(new Line2D.Float(6, 4, 6, 8));
                g2.draw(new Line2D.Float(8, 4, 8, 8));
                g2.draw(new Line2D.Float(10, 4, 10, 8));
                g2.draw(new Arc2D.Float(6, 6, 4, 4, 0, -180, Arc2D.OPEN));
                g2.draw(new Line2D.Float(8, 10, 8, 20));
                Path2D knife = new Path2D.Float();
                knife.moveTo(17, 4);
                knife.curveTo(17, 4, 19, 7, 19, 11);
                knife.lineTo(17, 11);
                knife.closePath();
                g2.fill(knife);
                g2.draw(new Line2D.Float(17, 11, 17, 20));
                break;

            case FOOD_FEEDBACK:
            case STAR:
                drawStar(g2, 12, 12, 9, 4.5);
                break;

            case USERS:
                // Security / Staff team
                g2.draw(new Ellipse2D.Float(6, 5, 6, 6));
                g2.draw(new Arc2D.Float(2, 13, 14, 10, 0, 180, Arc2D.OPEN));
                g2.draw(new Ellipse2D.Float(14, 6, 5, 5));
                g2.draw(new Arc2D.Float(12, 14, 10, 8, 0, 140, Arc2D.OPEN));
                break;

            case REPORTS:
                // Bar analytics chart
                g2.draw(new Line2D.Float(3, 3, 3, 21));
                g2.draw(new Line2D.Float(3, 21, 21, 21));
                g2.fill(new RoundRectangle2D.Float(5.5f, 13, 3.5f, 7, 1, 1));
                g2.fill(new RoundRectangle2D.Float(10.5f, 7, 3.5f, 13, 1, 1));
                g2.fill(new RoundRectangle2D.Float(15.5f, 10, 3.5f, 10, 1, 1));
                break;

            case PROFILE:
                // User round avatar
                g2.draw(new Ellipse2D.Float(2, 2, 20, 20));
                g2.fill(new Ellipse2D.Float(9, 6, 6, 6));
                g2.fill(new Arc2D.Float(5, 14, 14, 10, 0, 180, Arc2D.CHORD));
                break;

            case LOGOUT:
                // Door with exiting arrow
                g2.draw(new Line2D.Float(10, 4, 4, 4));
                g2.draw(new Line2D.Float(4, 4, 4, 20));
                g2.draw(new Line2D.Float(4, 20, 10, 20));
                g2.draw(new Line2D.Float(8, 12, 20, 12));
                g2.draw(new Line2D.Float(16, 8, 20, 12));
                g2.draw(new Line2D.Float(16, 16, 20, 12));
                break;

            case REFRESH:
                // Smooth circular reload arrow
                g2.draw(new Arc2D.Float(3.5f, 3.5f, 17, 17, 45, 270, Arc2D.OPEN));
                g2.fill(new Polygon(new int[]{18, 22, 16}, new int[]{4, 9, 11}, 3));
                break;

            case SEARCH:
                // Magnifying glass
                g2.draw(new Ellipse2D.Float(3.5f, 3.5f, 11, 11));
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Float(12.5f, 12.5f, 20, 20));
                break;

            case ADD:
                // Crisp plus glyph
                g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Line2D.Float(12, 4, 12, 20));
                g2.draw(new Line2D.Float(4, 12, 20, 12));
                break;

            case EDIT:
                // Pencil stylus
                Path2D pencil = new Path2D.Float();
                pencil.moveTo(17, 3);
                pencil.lineTo(21, 7);
                pencil.lineTo(7, 21);
                pencil.lineTo(3, 21);
                pencil.lineTo(3, 17);
                pencil.closePath();
                g2.draw(pencil);
                g2.draw(new Line2D.Float(14, 6, 18, 10));
                break;

            case DELETE:
                // Trash container
                g2.draw(new Line2D.Float(3, 6, 21, 6));
                g2.draw(new Line2D.Float(9, 3, 15, 3));
                Path2D bin = new Path2D.Float();
                bin.moveTo(5.5f, 6);
                bin.lineTo(6.5f, 20);
                bin.lineTo(17.5f, 20);
                bin.lineTo(18.5f, 6);
                g2.draw(bin);
                g2.draw(new Line2D.Float(10, 9.5f, 10, 16.5f));
                g2.draw(new Line2D.Float(14, 9.5f, 14, 16.5f));
                break;

            case EXPORT_CSV:
                // Download file
                g2.draw(new Line2D.Float(4, 16, 4, 20));
                g2.draw(new Line2D.Float(4, 20, 20, 20));
                g2.draw(new Line2D.Float(20, 20, 20, 16));
                g2.draw(new Line2D.Float(12, 3, 12, 14));
                g2.draw(new Line2D.Float(7.5f, 10, 12, 14.5f));
                g2.draw(new Line2D.Float(16.5f, 10, 12, 14.5f));
                break;

            case PRINT_PDF:
                // Printer
                g2.draw(new RoundRectangle2D.Float(3, 8, 18, 10, 2, 2));
                g2.draw(new Line2D.Float(6, 8, 6, 3));
                g2.draw(new Line2D.Float(6, 3, 18, 3));
                g2.draw(new Line2D.Float(18, 3, 18, 8));
                g2.draw(new Rectangle2D.Float(6, 14, 12, 7));
                g2.fill(new Ellipse2D.Float(17, 10.5f, 1.5f, 1.5f));
                break;

            case CHECK:
                // Bold checkmark
                g2.setStroke(new BasicStroke(2.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D check = new Path2D.Float();
                check.moveTo(4, 12);
                check.lineTo(9.5f, 17.5f);
                check.lineTo(20, 6);
                g2.draw(check);
                break;

            case LOCK:
                // Padlock
                g2.draw(new RoundRectangle2D.Float(4, 10, 16, 11, 3, 3));
                g2.draw(new Arc2D.Float(7, 3, 10, 14, 0, 180, Arc2D.OPEN));
                g2.fill(new Ellipse2D.Float(11, 13.5f, 2, 2));
                g2.draw(new Line2D.Float(12, 15, 12, 18));
                break;

            case INFO:
                g2.draw(new Ellipse2D.Float(2, 2, 20, 20));
                g2.fill(new Ellipse2D.Float(11, 6, 2, 2));
                g2.draw(new Line2D.Float(12, 10, 12, 17));
                break;

            case HOSTEL:
            case BUILDING:
                // Campus hostel building
                Path2D roof = new Path2D.Float();
                roof.moveTo(12, 2);
                roof.lineTo(22, 8);
                roof.lineTo(2, 8);
                roof.closePath();
                g2.fill(roof);
                g2.draw(new Rectangle2D.Float(4, 8, 16, 13));
                g2.fill(new Rectangle2D.Float(10, 15, 4, 6));
                g2.fill(new Rectangle2D.Float(6, 10, 3, 3));
                g2.fill(new Rectangle2D.Float(15, 10, 3, 3));
                break;

            case WARNING:
                Path2D tri = new Path2D.Float();
                tri.moveTo(12, 3);
                tri.lineTo(22, 20);
                tri.lineTo(2, 20);
                tri.closePath();
                g2.draw(tri);
                g2.draw(new Line2D.Float(12, 9, 12, 14));
                g2.fill(new Ellipse2D.Float(11, 16, 2, 2));
                break;

            case EYE:
                // Eye glyph
                Path2D eye = new Path2D.Float();
                eye.moveTo(2, 12);
                eye.curveTo(6, 6, 18, 6, 22, 12);
                eye.curveTo(18, 18, 6, 18, 2, 12);
                g2.draw(eye);
                g2.fill(new Ellipse2D.Float(9.5f, 9.5f, 5, 5));
                break;

            case EYE_OFF:
                Path2D eyeOff = new Path2D.Float();
                eyeOff.moveTo(2, 12);
                eyeOff.curveTo(6, 6, 18, 6, 22, 12);
                eyeOff.curveTo(18, 18, 6, 18, 2, 12);
                g2.draw(eyeOff);
                g2.draw(new Line2D.Float(3, 3, 21, 21));
                break;

            case CALENDAR:
                g2.draw(new RoundRectangle2D.Float(3, 4, 18, 17, 2, 2));
                g2.draw(new Line2D.Float(3, 9, 21, 9));
                g2.draw(new Line2D.Float(7, 2, 7, 5));
                g2.draw(new Line2D.Float(17, 2, 17, 5));
                g2.fill(new Rectangle2D.Float(6, 12, 2.5f, 2.5f));
                g2.fill(new Rectangle2D.Float(11, 12, 2.5f, 2.5f));
                g2.fill(new Rectangle2D.Float(16, 12, 2.5f, 2.5f));
                break;

            case BELL:
                // Notification bell
                Path2D bell = new Path2D.Float();
                bell.moveTo(12, 4);
                bell.curveTo(10, 4, 6, 7, 6, 14);
                bell.lineTo(4, 17);
                bell.lineTo(20, 17);
                bell.lineTo(18, 14);
                bell.curveTo(18, 7, 14, 4, 12, 4);
                bell.closePath();
                g2.draw(bell);
                g2.draw(new Arc2D.Float(10, 17, 4, 4, 0, -180, Arc2D.OPEN));
                break;

            case TREND_UP:
                // Upward trend arrow
                g2.draw(new Line2D.Float(4, 18, 11, 11));
                g2.draw(new Line2D.Float(11, 11, 15, 15));
                g2.draw(new Line2D.Float(15, 15, 20, 6));
                g2.draw(new Line2D.Float(14, 6, 20, 6));
                g2.draw(new Line2D.Float(20, 6, 20, 12));
                break;

            case FILTER:
                // Funnel filter
                Path2D fn = new Path2D.Float();
                fn.moveTo(3, 5);
                fn.lineTo(21, 5);
                fn.lineTo(14, 12);
                fn.lineTo(14, 19);
                fn.lineTo(10, 17);
                fn.lineTo(10, 12);
                fn.closePath();
                g2.draw(fn);
                break;

            case CHEVRON_LEFT:
                Path2D chevL = new Path2D.Float();
                chevL.moveTo(15, 5);
                chevL.lineTo(8, 12);
                chevL.lineTo(15, 19);
                g2.draw(chevL);
                break;

            case CHEVRON_RIGHT:
                Path2D chevR = new Path2D.Float();
                chevR.moveTo(9, 5);
                chevR.lineTo(16, 12);
                chevR.lineTo(9, 19);
                g2.draw(chevR);
                break;

            case MENU:
                g2.draw(new Line2D.Float(4, 6, 20, 6));
                g2.draw(new Line2D.Float(4, 12, 20, 12));
                g2.draw(new Line2D.Float(4, 18, 20, 18));
                break;
        }
    }

    private void drawStar(Graphics2D g2, double cx, double cy, double rOuter, double rInner) {
        Path2D star = new Path2D.Float();
        double angle = -Math.PI / 2;
        double step = Math.PI / 5;
        for (int i = 0; i < 10; i++) {
            double r = (i % 2 == 0) ? rOuter : rInner;
            double px = cx + Math.cos(angle) * r;
            double py = cy + Math.sin(angle) * r;
            if (i == 0) star.moveTo(px, py);
            else star.lineTo(px, py);
            angle += step;
        }
        star.closePath();
        g2.fill(star);
    }
}
