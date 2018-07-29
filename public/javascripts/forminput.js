


$(document).ready(function(){
    var tmpData = {};
    var parentElement = $(".sform-col").parent();
    var formStatus = 0;

    history.pushState(null,null,null);
    window.addEventListener("popstate", function(){
        if(formStatus != 0){
            history.pushState(null,null,null);
        }
    });

    $("#sform_button_cancel").click(function(){
        // ToDo
        console.log("cancel");
    });

    $("#sform_button_confirm").click(function(){
        // ToDo
        console.log("confirm");
        //console.log(tmpData);
        $(".sform-col-form-text").each(function(){
            tmpData[this.id] = this.value;
        });
        $(".sform-col-form-checkbox, .sform-col-form-radio").each(function(){
          var tmpChecked = [];
          $("[name=sel_" + this.id + "]").each(function(){
            if(this.checked) tmpChecked.push(this.value);
          })
          tmpData[this.id] = tmpChecked.join();
        })
        console.log(tmpData);
        console.log(this.tmpData);
        this.tmpData = tmpData;
        var that = this;

        $.ajax({
          type: "POST",
          url: "/sform/sfcon_php/formsave.php",
          dataType: "json",
          data: {
            mode : "validate",
            formid : $("#hashed_id").val(),
            postdata : tmpData
          },
          success: function(msg) {
            //console.log(msg);
            parentElement.empty();
            parentElement.append(msg);
            //console.log(tmpData);
            //console.log(that.tmpData);
            that.formStatus = 1;
            $("#sform_tmp").val(JSON.stringify(tmpData));
          }
        });

    });

    $("#sform_button_back").click(function(){
        // ToDo
        console.log("back");
    });

    $("#sform_button_submit").click(function(){
        // ToDo
        console.log("submit");
        tmpData = JSON.parse($("#sform_tmp").val());
        console.log(tmpData);
        $.ajax({
          type: "POST",
          url: "/sform/sfcon_php/formsave.php",
          dataType: "json",
          data: {
            mode : "save",
            formid : $("#hashed_id").val(),
            postdata : tmpData
          },
          success: function(msg) {
            console.log(msg);
            //parentElement.empty();
            //parentElement.append(msg);
            that.formStatus = 2;
          }
        });
    });



})

