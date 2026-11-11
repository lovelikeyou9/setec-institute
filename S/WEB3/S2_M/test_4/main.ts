//Basic
function calculateDiscount(amount: number, discount: number): number{
    amount = amount  + 1
    let discount_price: number = (discount/100) * amount
    let grand_total: number = amount - discount_price
    return grand_total
}


let grand_total: number = calculateDiscount(7, 30)
console.log(grand_total)

//optional parameter
function greeting(name: string = "No Name"): string{
    return `Hello: ${name}`
}

console.log(greeting("dara"))

