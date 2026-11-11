import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class NexusColors {
  static const background = Color(0xFFF7F9FB);
  static const surface = Color(0xFFF7F9FB);
  static const primary = Color(0xFF0066CC);
  static const primaryContainer = Color(0xFF004E9F);
  static const secondary = Color(0xFF00D2FF);
  static const onSurface = Color(0xFF191C1E);
  static const outline = Color(0xFFE2E8F0);
  static const outlineStrong = Color(0xFFC1C6D5);
  static const error = Color(0xFFBA1A1A);
  static const success = Color(0xFF15803D);
  static const lowStock = Color(0xFFEA580C);
}

class AppTheme {
  static ThemeData get light {
    const radiusSm = 4.0;
    const radiusMd = 8.0;

    final base = ThemeData(
      useMaterial3: true,
      brightness: Brightness.light,
      scaffoldBackgroundColor: NexusColors.background,
      colorScheme: const ColorScheme.light(
        surface: NexusColors.surface,
        primary: NexusColors.primary,
        onPrimary: Colors.white,
        primaryContainer: Color(0xFFD6E7F8),
        onPrimaryContainer: Color(0xFF003A75),
        secondary: NexusColors.secondary,
        onSecondary: Colors.white,
        secondaryContainer: Color(0xFFD6E7F8),
        onSecondaryContainer: Color(0xFF003A75),
        onSurface: NexusColors.onSurface,
        outline: NexusColors.outline,
        error: NexusColors.error,
        onError: Colors.white,
      ),
    );

    final heading = GoogleFonts.spaceGroteskTextTheme(base.textTheme);
    final body = GoogleFonts.interTextTheme(heading);
    final mono = GoogleFonts.jetBrainsMono();

    return base.copyWith(
      textTheme: body.apply(bodyColor: NexusColors.onSurface, displayColor: NexusColors.onSurface),
      appBarTheme: AppBarTheme(
        elevation: 0,
        scrolledUnderElevation: 0,
        backgroundColor: NexusColors.background,
        foregroundColor: NexusColors.onSurface,
        titleTextStyle: GoogleFonts.spaceGrotesk(
          fontSize: 18,
          fontWeight: FontWeight.w700,
          color: NexusColors.onSurface,
        ),
      ),
      cardTheme: CardThemeData(
        color: Colors.white,
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(radiusMd),
          side: const BorderSide(color: NexusColors.outline),
        ),
        margin: EdgeInsets.zero,
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: Colors.white,
        contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 12),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(radiusSm),
          borderSide: const BorderSide(color: NexusColors.outline),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(radiusSm),
          borderSide: const BorderSide(color: NexusColors.outline),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(radiusSm),
          borderSide: const BorderSide(color: NexusColors.primary, width: 1.5),
        ),
        labelStyle: GoogleFonts.inter(fontSize: 13),
        hintStyle: GoogleFonts.inter(fontSize: 13, color: NexusColors.outlineStrong),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: NexusColors.primary,
          foregroundColor: Colors.white,
          elevation: 0,
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(radiusSm)),
          textStyle: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w600, fontSize: 14),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: NexusColors.primary,
          side: const BorderSide(color: NexusColors.outlineStrong),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(radiusSm)),
          textStyle: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w600, fontSize: 14),
        ),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: NexusColors.primary,
          textStyle: GoogleFonts.spaceGrotesk(fontWeight: FontWeight.w600),
        ),
      ),
      chipTheme: ChipThemeData(
        backgroundColor: Colors.white,
        selectedColor: NexusColors.primary,
        disabledColor: NexusColors.outline,
        checkmarkColor: Colors.white,
        deleteIconColor: NexusColors.outlineStrong,
        labelStyle: GoogleFonts.inter(
          fontSize: 12,
          fontWeight: FontWeight.w600,
          color: NexusColors.onSurface,
        ),
        secondaryLabelStyle: GoogleFonts.inter(
          fontSize: 12,
          fontWeight: FontWeight.w600,
          color: Colors.white,
        ),
        side: const BorderSide(color: NexusColors.outline),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
        padding: const EdgeInsets.symmetric(horizontal: 4),
        showCheckmark: true,
      ),
      dividerTheme: const DividerThemeData(color: NexusColors.outline, thickness: 1),
      extensions: [NexusTypography(mono: mono)],
    );
  }
}

class NexusTypography extends ThemeExtension<NexusTypography> {
  const NexusTypography({required this.mono});

  final TextStyle mono;

  @override
  NexusTypography copyWith({TextStyle? mono}) => NexusTypography(mono: mono ?? this.mono);

  @override
  NexusTypography lerp(ThemeExtension<NexusTypography>? other, double t) {
    if (other is! NexusTypography) return this;
    return NexusTypography(mono: TextStyle.lerp(mono, other.mono, t)!);
  }
}

extension NexusThemeContext on BuildContext {
  TextStyle get monoStyle =>
      Theme.of(this).extension<NexusTypography>()?.mono ??
      GoogleFonts.jetBrainsMono();
}
