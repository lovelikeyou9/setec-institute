<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (Schema::hasTable('products') && Schema::hasColumn('products', 'gallery_urls')) {
            DB::statement('ALTER TABLE products MODIFY `gallery_urls` TEXT NULL');
        }
    }

    public function down(): void
    {
        if (Schema::hasTable('products') && Schema::hasColumn('products', 'gallery_urls')) {
            DB::statement('ALTER TABLE products MODIFY `gallery_urls` VARCHAR(1000) NULL');
        }
    }
};
