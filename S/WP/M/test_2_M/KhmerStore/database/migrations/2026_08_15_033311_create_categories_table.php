<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('categories', function (Blueprint $table) {
            $table->integerIncrements('id');
            $table->string('category_name', 100)->index()->unique();
            $table->tinyInteger('parent_id')->unsigned();
            $table->tinyInteger('level')->default(0);
            $table->string('description', 255)->nullable();
            $table->tinyInteger('status', [0,1])->default(0);
            $table->tinyInteger('trash', [0,1])->default(0);

            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('categories');
    }
};
