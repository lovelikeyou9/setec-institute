//Basic
function calculateDiscount(amount, discount) {
    amount = amount + 1;
    var discount_price = (discount / 100) * amount;
    var grand_total = amount - discount_price;
    return grand_total;
}
var grand_total = calculateDiscount(7, 30);
console.log(grand_total);
//optional parameter
function greeting(name) {
    return "Hello: ".concat(name || 'No Name');
}
console.log(greeting("dara"));
console.log(greeting());
