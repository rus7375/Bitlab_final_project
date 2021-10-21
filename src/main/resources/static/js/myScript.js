function addToBasket(itemId) {
    var basket = JSON.parse(localStorage.getItem("basket"));

    if(!basket) basket = [];
    basket.push(itemId);
    localStorage.setItem("basket", JSON.stringify(basket));
    document.getElementById("basketButtonId").innerText = "Added";

}

function getItems() {
    var basket = JSON.parse(localStorage.getItem("basket"));

    document.getElementById("item_id").value = basket;
    document.getElementById("basket_form_id").submit();

}

function removeFromBasket(itemId) {
    var basket = JSON.parse(localStorage.getItem("basket"));
  for(let i = 0; i < basket.length; i++) {
      if(basket[i] == itemId){
          basket.splice(i, 1);
          break;
      }
  }
    localStorage.setItem("basket", JSON.stringify(basket));

    getItems()
}

function clearBasket() {
    localStorage.setItem("basket", null);
    getItems()
}

