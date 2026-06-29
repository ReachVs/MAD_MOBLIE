package com.example.mad_final.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

class DatabaseSeeder : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        seedServices(db)
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        seedServices(db)
    }

    private fun seedServices(db: SupportSQLiteDatabase) {
        db.execSQL("DELETE FROM services")
        val services = listOf(
            // MAINTENANCE SERVICES - Fluid Maintenance
            "('maint_fluid_oil_insp', 'Engine Oil Inspection', 15.0, '15m', 'Regular engine oil check and top-up if necessary.', 'maintenance', 'maintenance,oil,fluids', 'MAINTENANCE SERVICES', 'Fluid Maintenance')",
            "('maint_fluid_filter_insp', 'Oil Filter Inspection', 10.0, '10m', 'Check condition of the oil filter.', 'maintenance', 'maintenance,filter', 'MAINTENANCE SERVICES', 'Fluid Maintenance')",
            "('maint_fluid_coolant_insp', 'Coolant Level Inspection', 10.0, '10m', 'Verify coolant levels and check for leaks.', 'maintenance', 'maintenance,coolant', 'MAINTENANCE SERVICES', 'Fluid Maintenance')",
            "('maint_fluid_brake_insp', 'Brake Fluid Inspection', 12.0, '10m', 'Ensure brake fluid is at optimal levels and clean.', 'maintenance', 'maintenance,brake,fluids', 'MAINTENANCE SERVICES', 'Fluid Maintenance')",
            "('maint_fluid_clutch_insp', 'Clutch Fluid Inspection', 12.0, '10m', 'Check clutch fluid levels for smooth operation.', 'maintenance', 'maintenance,clutch,fluids', 'MAINTENANCE SERVICES', 'Fluid Maintenance')",

            // MAINTENANCE SERVICES - Brake System
            "('maint_brake_pad', 'Brake Pad Inspection', 15.0, '15m', 'Check brake pad wear and tear.', 'maintenance', 'maintenance,brake,pads', 'MAINTENANCE SERVICES', 'Brake System')",
            "('maint_brake_disc', 'Brake Disc Inspection', 15.0, '10m', 'Inspect brake discs for scoring or warping.', 'maintenance', 'maintenance,brake,disc', 'MAINTENANCE SERVICES', 'Brake System')",
            "('maint_brake_caliper', 'Brake Caliper Inspection', 20.0, '20m', 'Inspect brake calipers for proper function.', 'maintenance', 'maintenance,brake,caliper', 'MAINTENANCE SERVICES', 'Brake System')",
            "('maint_brake_cable', 'Brake Control Cable Adjustment', 15.0, '15m', 'Adjust brake cables for better response.', 'maintenance', 'maintenance,brake,cable', 'MAINTENANCE SERVICES', 'Brake System')",

            // MAINTENANCE SERVICES - Suspension & Steering
            "('maint_susp_steering', 'Steering Head Bearing Inspection', 30.0, '30m', 'Check for steering play and bearing condition.', 'maintenance', 'maintenance,steering,bearing', 'MAINTENANCE SERVICES', 'Suspension & Steering')",
            "('maint_susp_fork', 'Front Fork Inspection', 20.0, '20m', 'Check front forks for leaks and alignment.', 'maintenance', 'maintenance,suspension,fork', 'MAINTENANCE SERVICES', 'Suspension & Steering')",
            "('maint_susp_shock', 'Rear Shock Absorber Inspection', 20.0, '20m', 'Inspect rear shocks for damping and leaks.', 'maintenance', 'maintenance,suspension,shock', 'MAINTENANCE SERVICES', 'Suspension & Steering')",

            // MAINTENANCE SERVICES - Drive System
            "('maint_drive_tension', 'Drivetrain Tension Inspection', 15.0, '15m', 'Check and adjust drive chain/belt tension.', 'maintenance', 'maintenance,drive,tension', 'MAINTENANCE SERVICES', 'Drive System')",
            "('maint_drive_alignment', 'Drivetrain Alignment Inspection', 15.0, '15m', 'Ensure rear wheel and drivetrain alignment.', 'maintenance', 'maintenance,drive,alignment', 'MAINTENANCE SERVICES', 'Drive System')",
            "('maint_drive_lubrication', 'Drivetrain Lubrication', 10.0, '10m', 'Lubricate the drive chain/belt.', 'maintenance', 'maintenance,drive,lubrication', 'MAINTENANCE SERVICES', 'Drive System')",

            // MAINTENANCE SERVICES - Electrical System
            "('maint_elec_battery', 'Battery Inspection', 15.0, '15m', 'Test battery voltage and health.', 'maintenance', 'maintenance,electrical,battery', 'MAINTENANCE SERVICES', 'Electrical System')",
            "('maint_elec_charging', 'Charging System Inspection', 25.0, '20m', 'Verify alternator/stator and regulator output.', 'maintenance', 'maintenance,electrical,charging', 'MAINTENANCE SERVICES', 'Electrical System')",
            "('maint_elec_indicator', 'Indicator Inspection', 10.0, '10m', 'Check all signal lights and indicators.', 'maintenance', 'maintenance,electrical,lights', 'MAINTENANCE SERVICES', 'Electrical System')",

            // WASHING
            "('wash_basic', 'Basic Wash', 25.0, '30m', 'A quick exterior wash to remove surface dirt.', 'washing', 'washing,clean', 'WASHING', NULL)",
            "('wash_normal', 'Normal Wash', 45.0, '60m', 'Detailed wash including degreasing and polishing.', 'washing', 'washing,clean,polish', 'WASHING', NULL)",
            "('wash_premium', 'Premium Wash', 85.0, '120m', 'Full detailing, wax coating, and deep cleaning.', 'washing', 'washing,clean,detail,wax', 'WASHING', NULL)",

            // ENGINE CHECK UP
            "('eng_insp_ignition', 'Ignition & Air Intake Inspection', 110.0, '45m', 'Check spark plugs and air filters.', 'engine_checkup', 'engine,ignition,air', 'ENGINE CHECK UP', 'Engine Inspection')",
            "('eng_insp_fuel', 'Fuel System Inspection', 130.0, '60m', 'Check fuel lines and injector performance.', 'engine_checkup', 'engine,fuel', 'ENGINE CHECK UP', 'Engine Inspection')",
            "('eng_insp_health', 'Engine Health Inspection', 180.0, '90m', 'Comprehensive engine diagnostic and compression test.', 'engine_checkup', 'engine,health', 'ENGINE CHECK UP', 'Engine Inspection')",

            // TUNING PERFORMANCE
            "('tune_dyno_1000', 'Dyno Tuning – 1000cc & Above', 450.0, '180m', 'Professional dyno tuning for high-capacity bikes.', 'tuning', 'tuning,performance,dyno', 'TUNING PERFORMANCE', 'Dyno Tuning')",
            "('tune_dyno_600', 'Dyno Tuning – 600cc Class', 350.0, '150m', 'Optimized dyno tuning for middleweight bikes.', 'tuning', 'tuning,performance,dyno', 'TUNING PERFORMANCE', 'Dyno Tuning')",
            "('tune_diag_ecu', 'ECU Diagnostics', 120.0, '45m', 'Read and clear fault codes, optimize ECU maps.', 'tuning', 'tuning,electrical,ecu', 'TUNING PERFORMANCE', 'Diagnostic Systems')",
            "('tune_diag_abs', 'ABS Inspection', 90.0, '30m', 'Diagnostic check for anti-lock braking system.', 'tuning', 'tuning,brake,abs', 'TUNING PERFORMANCE', 'Diagnostic Systems')"
        )

        services.forEach { values ->
            db.execSQL("INSERT INTO services (id, title, price, duration, description, imageUrl, tags, category, subCategory) VALUES $values")
        }
    }
}
