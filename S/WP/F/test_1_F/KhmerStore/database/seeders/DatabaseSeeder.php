<?php

namespace Database\Seeders;

use App\Models\Brand;
use App\Models\Category;
use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        User::factory()->create([
            'name' => 'Test User',
            'email' => 'test@example.com',
        ]);

        Category::create([
            'category_name' => 'Drinks',
            'parent_id' => 0,
            'description' => 'Beverage products',
            'status' => 1,
        ]);

        Category::create([
            'category_name' => 'Electronics',
            'parent_id' => 0,
            'description' => 'Electronic products',
            'status' => 1,
        ]);

        Brand::create([
            'brand_name' => 'Coca-Cola',
            'description' => 'Soft drink brand',
        ]);

        Brand::create([
            'brand_name' => 'Samsung',
            'description' => 'Electronics brand',
        ]);
    }
}
