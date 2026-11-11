<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        if (! Schema::hasColumn('products', 'option_groups')) {
            Schema::table('products', function (Blueprint $table) {
                $table->text('option_groups')->nullable()->after('ram_options');
            });
        }
    }

    public function down(): void
    {
        if (Schema::hasColumn('products', 'option_groups')) {
            Schema::table('products', function (Blueprint $table) {
                $table->dropColumn('option_groups');
            });
        }
    }
};
