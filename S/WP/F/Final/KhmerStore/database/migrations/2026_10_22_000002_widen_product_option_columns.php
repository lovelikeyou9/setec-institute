<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (! Schema::hasTable('products')) {
            return;
        }
        foreach (['color_options', 'storage_options', 'ram_options'] as $col) {
            if (Schema::hasColumn('products', $col)) {
                DB::statement("ALTER TABLE products MODIFY `{$col}` VARCHAR(500) NULL");
            }
        }
    }

    public function down(): void
    {
        if (! Schema::hasTable('products')) {
            return;
        }
        foreach (['color_options', 'storage_options', 'ram_options'] as $col) {
            if (Schema::hasColumn('products', $col)) {
                DB::statement("ALTER TABLE products MODIFY `{$col}` VARCHAR(100) NULL");
            }
        }
    }
};
