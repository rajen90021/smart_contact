console.log("this is script file ")

// function togglesidebar(){



// }
// or
const togglesidebar= ()=>{

    if($(".sidebar").is(":visible")){
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }
    else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
};
const search=()=>{
console.log("searching......");

let query= $("#search-input").val();

if( query==""){

$(".search-result").hide();

}else{


    console.log(query);

    //sending request to server

    let url = `http://localhost:8080/search/${query}`;

    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        //data......
        // console.log(data);

        let text = `<div class='list-group'>`;

        for (var i = 0; i < data.length; i++) {
            var contact = data[i];
            text += "<a href='/user/" + contact.cId + "/contact' class='list-group-item list-group-item-action'>" + contact.name + "</a>";
        }
        text += `</div>`;

        $(".search-result").html(text);
        $(".search-result").show();
      });

}


}

const paymentstart=()=>{
  console.log("payment started..");
  var amount= $("#payment_field").val();
  console.log(amount);
  if(amount=="" || amount==null){
    alert("amount is required !!");
    return;
  }
}


