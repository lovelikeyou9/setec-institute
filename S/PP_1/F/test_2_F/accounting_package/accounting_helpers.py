
# Module
def to_khr(usd_amount: float, khr_rate:float = 4000 ) -> str:
    """
    Notes:
        - Uses a fixed exchange rate of 4,035 KHR per 1 USD.
        - Always rounds up to the nearest 100៛.
    """
    import math
    khr_amount = math.ceil((float(usd_amount) * khr_rate) / 100 ) * 100
    khr_amount = f"{khr_amount:,.2f}៛"
    return khr_amount