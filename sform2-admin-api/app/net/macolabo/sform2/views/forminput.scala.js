@(path:String)
$(document).ready(function(){
    var tmpData = {};
    var tmpForm;
    var parentElement = $(".sform-col").parent();
    var formStatus = 0;
    var that = this;

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
        var path = "@(path)".replace("\\","");

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
        this.tmpData = tmpData;
        var _that = that;

        $.ajax({
          type: "POST",
          url: path,
          dataType: "json",
          data: {
            mode : "validate",
            formid : $("#hashed_id").val(),
            postdata : tmpData
          },
          success: function(msg) {
            that.tmpForm = parentElement.clone(true);
            parentElement.empty();
            parentElement.append(msg);
            if($("#validate_result").val() == 'OK'){
              that.formStatus = 1;
            } else {
              that.formStatus = 0;
            }
            $("#sform_tmp").val(JSON.stringify(tmpData));
          }
        });

    });

    $("#sform_button_back").click(function(){
        // ToDo
        console.log("back");
        parentElement.empty();
        parentElement.append(that.tmpForm[0].innerHTML);
        that.formStatus = 0;


    });

    $("#sform_button_submit").click(function(){
        // ToDo
        console.log("submit");
        tmpData = JSON.parse($("#sform_tmp").val());
        var path = "@(path)".replace("\\","");
        var that = this;
        $.ajax({
          type: "POST",
          url: path,
          dataType: "json",
          data: {
            mode : "save",
            formid : $("#hashed_id").val(),
            postdata : tmpData
          },
          success: function(msg) {
            parentElement.empty();
            parentElement.append(msg);
            that.formStatus = 2;
          },
          error: function(msg){
            console.log("failed");
            console.log(msg);
          }
        });
    });

})


