<?php

namespace App\Casts;

use Illuminate\Contracts\Database\Eloquent\CastsAttributes;
use Illuminate\Database\Eloquent\Model;

class MysqlBit implements CastsAttributes
{
    public function get(Model $model, string $key, mixed $value, array $attributes): bool
    {
        if ($value === null) {
            return false;
        }
        if (is_bool($value)) {
            return $value;
        }
        if (is_int($value) || is_float($value)) {
            return (int) $value === 1;
        }
        if (is_string($value)) {
            if ($value === '' || $value === '0' || $value === "\x00") {
                return false;
            }
            if ($value === '1' || $value === "\x01") {
                return true;
            }

            return ord($value[0]) === 1;
        }

        return (bool) $value;
    }

    public function set(Model $model, string $key, mixed $value, array $attributes): int
    {
        return filter_var($value, FILTER_VALIDATE_BOOLEAN) ? 1 : 0;
    }
}
