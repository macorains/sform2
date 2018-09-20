    var app = new Vue({
        el: "#app",
        data : sfdata,
        components : sformComponents,
        created : function(){
          var that = this;

          // csrf
          jQuery(document).ajaxSend(function(event,jqxhr,settings){
              jqxhr.setRequestHeader('Csrf-Token', jQuery("input[name=csrfToken]").val());
          });

          // Transferリスト取得
          var reqdata = {objtype: "Transfer", action: "getTransferList", rcdata: {}};
          jQuery.ajax({
              type: "POST",
              url: "rc/",
              dataType: "json",
              contentType: "application/json",
              data: JSON.stringify(reqdata),
              success: function(msg) {
                  that.transferList = msg.dataset;
              }
          });

        },
        methods: {
            logout(){
                location.href="signOut";
            },
            dragstart(item, e) {
                if (!this.inColEdit) {
                    this.draggingItem = item
                    e.target.style.opacity = 0.5;
                }
                e.dataTransfer.effectAllowed = "move";
                e.dataTransfer.dropEffect = "move";
            },
            dragend(e) {
                e.target.style.opacity = 1;
            },
            dragenter(item) {
                if (!this.inColEdit) {
                    const tempIndex = item.index;
                    item.index = this.draggingItem.index
                    this.draggingItem.index = tempIndex
                }
            },
            // ページ切り替え
            togglePage(pagename,m) {
                jQuery(".sfpage").hide();
                jQuery("#" + pagename).show();
                for (s in this.menuStatus) {
                    this.menuStatus[s] = "";
                };
                this.menuStatus[m] = "active";
            },
            // フォーム追加
            addForm(){
                var i = Object.keys(this.formlist).length;
                var tmp = {
                    index: i+'',
                    id: "",
                    status: "0",
                    name: "フォーム" + i,
                    title: "フォーム" + i,
                    extLink1: false,
                    cancelUrl: "",
                    completeUrl: "",
                    inputHeader: "",
                    confirmHeader: "",
                    completeText: "",
                    closeText: "",
                    replymailFrom: "",
                    replymailSubject: "",
                    replymailText: "",
                    noticemailSend: "",
                    noticemailText: "",
                    formCols: {
                        0: {
                            index: "0",
                            name: "メールアドレス",
                            colId: "email",
                            coltype: "1",
                            default: "",
                            validations: {
                                inputType: "5",
                                minValue: "0",
                                maxValue: "0",
                                minLength: "0",
                                maxLength: "0",
                                required: true
                            },
                        }
                    }
                }
                var reqdata = {
                    objtype: "Form",
                    action: "create",
                    rcdata: {formDef: tmp, transferTasks : {}}
                };
                jQuery.ajax({
                    type: "POST",
                    url: "rc/",
                    dataType: "json",
                    contentType: "application/json",
                    data: JSON.stringify(reqdata),
                    success: function(msg) {
                        console.log(msg);
                        var dt = msg.dataset;
                        tmp.id = dt.id;
                        Vue.set(app.formlist,i,tmp);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        console.log(textStatus);
                        console.log(errorThrown);
                    }
                });
            },
            // フォーム状態変更
            changeFormStatus(idx, st) {
                this.formlist[idx].status = st;
                this.tmpFormInput = this.formlist[idx];
                this.tmpFormCols = this.tmpFormInput["formCols"];
                this.endFormEdit(1, idx);
            },
            // フォーム編集開始
            startFormEdit(type, idx) {
                var that = this;
                this.tmpFormInput = this.formlist[idx];
                this.tmpFormCols = this.tmpFormInput["formCols"];
                this.tmpFormColInput = {
                    index: "0",
                    name: "",
                    colId: "",
                    coltype: "",
                    default: "",
                    validations: {
                        inputType: "0",
                        minValue: "0",
                        maxValue: "0",
                        minLength: "0",
                        maxLength: "0"
                    },
                };
                //this.tmpTransferRuleSetting['sformCol'] = "";
                //this.tmpTransferRuleSetting['sfCol'] = "";
                this.tmpTransferRuleSetting['sformColConvert'] = {};
                this.tmpTransferRuleSetting['sfColConvert'] = {};
                for(col in this.tmpFormCols){
                      this.tmpTransferRuleSetting['sformColConvert'][this.tmpFormCols[col].colId] = this.tmpFormCols[col].name;
                }
                // TransferTask取得
                var reqdata = {
                    objtype: "TransferTask",
                    action: "getTransferTaskListByFormId",
                    rcdata: {"formId" : this.tmpFormInput.hashed_id}
                };
                jQuery.ajax({
                    type: "POST",
                    url: "rc/",
                    dataType: "json",
                    contentType: "application/json",
                    data: JSON.stringify(reqdata),
                    success: function(msg) {
                        that.tmpTransferTaskList = Array.isArray(msg.dataset)?msg.dataset:[];

                        for(var task in that.tmpTransferTaskList){
                            var transferTypeId = that.tmpTransferTaskList[task].transfer_type_id;
                            var transferName = that.transferList.filter(function(item,index){
                                if(item.type_id == transferTypeId) return true;
                            })
                            that.tmpTransferTaskList[task].transfer_name = transferName[0]['name'];
                        }
                    }
                });

                jQuery(".sfpage,.formColEditOn").hide();
                jQuery("#formEdit").show();
            },
            // フォーム編集キャンセル
            cancelFormEdit() {
                jQuery(".sfpage").hide();
                jQuery("#formList").show();
                this.hideFormColEdit();
            },
            // フォーム編集完了
            endFormEdit(type, idx) {
                var that = this;
                switch (type) {
                    case 1:
                        this.tmpFormInput["formCols"] = this.tmpFormCols;
                        this.formlist[idx] = this.tmpFormInput;
                        jQuery(".colEditPanel").hide();
                        jQuery(".sfpage,.formColEditOn").hide();
                        jQuery("#formList,.formColEditOff").show();
                        var reqdata = {
                            objtype: "Form",
                            action: "create",
                            rcdata: {formDef: this.tmpFormInput, transferTasks : this.tmpTransferTaskList}
                        };
                        jQuery.ajax({
                            type: "POST",
                            url: "rc/",
                            dataType: "json",
                            contentType: "application/json",
                            data: JSON.stringify(reqdata),
                            success: function(msg) {
                                console.log(msg.dataset);
                                console.log(that.tmpFormInput);
                                //var dt = JSON.parse(msg.dataset);
                                var dt = msg.dataset;
                                that.formlist[idx].id = dt.id;
                                //app.formlist = JSON.parse(msg.dataset);
                            },
                            error: function(XMLHttpRequest, textStatus, errorThrown) {
                                console.log(textStatus);
                                console.log(errorThrown);
                            }
                        });
                        break;
                    case 2:
                        break;
                }
            },
            // フォーム削除確認
            deleteFormConfirm(idx,id) {
                /* Todo */
                this.deleteId = id;
                this.deleteIdx = idx;
                jQuery("#modal_delete_form").modal("show");
            },
            // フォーム削除実行
            deleteForm(idx) {
                /* Todo */
                var that = this;
                var reqdata = {
                    objtype: "Form",
                    action: "delete",
                    rcdata: {
                        id : this.deleteId + ''
                    }
                };
                jQuery.ajax({
                    type: "POST",
                    url: "rc/",
                    dataType: "json",
                    contentType: "application/json",
                    data: JSON.stringify(reqdata),
                    success: function(msg) {
                        Vue.delete(app.formlist, that.deleteIdx);
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        console.log(textStatus);
                        console.log(errorThrown);
                    }
                });

                jQuery("#modal_delete_form").modal("hide");
            },
            // フォーム項目追加                
            addFormCol() {
                var i = Object.keys(this.tmpFormCols).length;
                var tmp = {
                    index: i+'',
                    name: "項目" + i,
                    colId: "col" + i,
                    coltype: "1",
                    default: "",
                    validations: {
                        inputType: "0",
                        minValue: "0",
                        maxValue: "0",
                        minLength: "0",
                        maxLength: "0"
                    },
                    selectList : {}
                };
                Vue.set(app.tmpFormCols,i,tmp);
            },
            // フォーム項目削除確認
            deleteFormColConfirm(fid, idx) {
                this.deleteIdx = idx;
                jQuery("#modal_delete_formcol").modal("show");
            },
            // フォーム項目削除
            deleteFormCol() {
                Vue.delete(app.tmpFormCols, this.deleteIdx);
                jQuery("#modal_delete_formcol").modal("hide");
            },
            // フォーム項目並び替え
            reorderFormCol(){
                jQuery("#modal_reorder_formcol").modal('show');
            },
            // フォーム項目編集開始
            startFormColEdit(fid, idx) {
                this.tmpFormColInput = this.tmpFormCols[idx];
                this.tmpFormColSelect = _.orderBy(this.tmpFormColInput["selectList"],"index");
                jQuery("#modal_edit_formcol").modal('show');
                console.log(this.tmpFormColInput);
            },
            // フォーム項目編集キャンセル
            cancelFormColEdit(idx) {
                this.inColEdit = false;
                this.hideFormColEdit();
            },
            // フォーム項目編集終了
            endFormColEdit(fid, idx) {
                this.tmpFormColInput["selectList"] = this.tmpFormColSelect;
                this.tmpFormCols[idx] = this.tmpFormColInput;
                this.hideFormColEdit();
                this.inColEdit = false;
            },
            // フォーム項目内選択項目追加
            addFormColSel() {
                var i = this.tmpFormColSelect == null ? 0 : Object.keys(this.tmpFormColSelect).length;
                var tmp = {
                    index: i+'',
                    displayText: "選択項目" + i,
                    value: i+'',
                    default: "false",
                    viewStyle: "display:inline",
                    editStyle: "display:none"
                };
                if(0 == i) app.tmpFormColSelect = {};
                Vue.set(app.tmpFormColSelect,i,tmp);

            },
            // フォーム項目内選択項目削除
            deleteFormColSel(idx) {
                Vue.delete(app.tmpFormColSelect, idx);
                var c = 0;
                var tmp = {};
                for (s in this.tmpFormColSelect) {
                    tmp[c] = this.tmpFormColSelect[s];
                    tmp[c]["idx"] = c;
                    c++;
                }
                this.tmpFormColSelect = tmp;
            },
            // フォーム項目内選択項目編集開始
            startFormColSelEdit(idx) {
                for (s in this.tmpFormColSelect) {
                    this.tmpFormColSelect[s]["viewStyle"] = "display:inline";
                    this.tmpFormColSelect[s]["editStyle"] = "display:none";
                }
                jQuery(".selView").show();
                jQuery(".selEdit").hide();
                this.tmpFormColSelect[idx]["viewStyle"] = "display:none";
                this.tmpFormColSelect[idx]["editStyle"] = "display:inline";
                this.tmpFormColSelectBackup["displayText"] = this.tmpFormColSelect[idx]["displayText"];
                this.tmpFormColSelectBackup["value"] = this.tmpFormColSelect[idx]["value"];
            },
            // フォーム項目内選択項目編集キャンセル
            cancelFormColSelEdit(idx) {
                this.tmpFormColSelect[idx]["displayText"] = this.tmpFormColSelectBackup["displayText"];
                this.tmpFormColSelect[idx]["value"] = this.tmpFormColSelectBackup["value"];
                this.endFormColSelEdit();
            },
            // フォーム項目内選択項目編集完了
            endFormColSelEdit(idx) {
                for (s in this.tmpFormColSelect) {
                    this.tmpFormColSelect[s]["viewStyle"] = "display:inline";
                    this.tmpFormColSelect[s]["editStyle"] = "display:none";
                }
                jQuery(".selView").show();
                jQuery(".selEdit").hide();
            },
            // フォーム送信データ一覧表示
            startFormData(index) {
                jQuery.ajax({
                    type: "GET",
                    url: "formpost/" + this.formlist[index].hashed_id,
                    //dataType: "json",
                    contentType: "application/json",
                    success: function(msg) {
                        var getData = function(src, cols){
                            return cols.map(x=>typeof src[x] == "undefined"?"":src[x]);
                        }
                        var cols = function(src){
                            var res = [];
                            for(s in src){
                                var d = {};
                                d['data'] = src[s].colId
                                res.push(d);
                            }
                            return res;
                        }
                        var dt = msg.rows.map(x=>getData(x, cols(msg.cols)));
                        console.log(dt);
                        jQuery("#formDataTable").DataTable({
                            data: dt,
                            columns: cols(msg.cols)
                        });

                        //jQuery("#formDataTable").DataTable({
                        /*
                        var formData = []
                        var cols = [];
                        var colName = JSON.parse(msg.message);
                        for(var i in msg.dataset){
                            var tmp = JSON.parse(msg.dataset[i].postdata)
                            for( var t in tmp){
                                cols.push({ data : t, title : colName[t]});
                            }
                            break;
                        }
                        for(var i in msg.dataset){
                            formData.push(JSON.parse(msg.dataset[i].postdata));
                        }

                        jQuery("#formDataTable").DataTable({
                            data: formData,
                            columns: cols
                        });
                        */
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        console.log(textStatus);
                        console.log(errorThrown);
                    }
                });
            },
            // ユーザー編集開始
            startUserEdit(idx) {
                jQuery(".sfpage,.formColEditOn").hide();
                jQuery("#userEdit").show();
                this.tmpUserInput = this.userList[idx];
            },
            // ユーザー編集キャンセル
            cancelUserEdit() {
                jQuery(".sfpage,.formColEditOn").hide();
                jQuery("#userList").show();
            },
            // ユーザー状態変更 
            changeUserStatus(idx, st) {
                // ToDo
            },
            // ユーザー編集完了
            endUserEdit(idx) {
                this.userList[idx] = this.tmpUserInput;
                jQuery(".sfpage,.formColEditOn").hide();
                jQuery("#userList").show();

            },
            // ユーザー削除確認
            deleteUserConfirm(idx) {
                // ToDo
                jQuery("#modal_delete_user").modal("show");
            },
            // ユーザー削除
            deleteUser(idx) {
                // ToDo
                jQuery("#modal_delete_user").modal("hide");
            },
            // ユーザーパスワードリセット確認
            resetPasswordConfirm(idx) {
                // ToDo 
            },
            // ユーザーパスワードリセット
            resetPassword(idx) {
                // ToDo 
            },
            // フォーム項目編集modal消去
            hideFormColEdit() {
                jQuery(".colEditPanel").hide();
                jQuery(".formColEditOn").hide();
                jQuery(".formColEditOff").show();
            },
            // フォームデータ詳細表示
            showFormPostData(idx) {
                console.log(this.formPostData);
                this.selectedFormPostDataIdx = idx;
                jQuery("#modal_list_formdata").modal("show");
            },
            // フォームデータ削除確認
            deleteFormPostDataConfirm(idx) {
                // ToDo
                jQuery("#modal_delete_formdata").modal("show");

            },
            // フォームデータ削除
            deleteFormPostData(idx) {
                // ToDo
                jQuery("#modal_delete_formdata").modal("hide");
            },
            // CSVダウンロード確認
            downloadCsvConfirm() {
                // ToDo
                jQuery("#modal_download_csv").modal("show");
            },
            // CSVダウンロード
            downloadCsv() {
                // ToDo
                jQuery("#modal_download_csv").modal("hide");
            },
            getFormDataColLabel(idx) {
                if (typeof this.formPostData.column[idx] != "undefined") {
                    if (typeof this.formPostData.column[idx]["label"] != "undefined") {
                        return this.formPostData.column[idx]["label"];
                    }
                }
                return "";
            },
            TransferConfigChangeTransfer(transfer){
                jQuery('.transfer-config-selected').removeClass('transfer-config-selected');
                jQuery('.transfer-config-panel-show').addClass('transfer-panel').removeClass('transfer-config-panel-show');
                jQuery("#item-transfer-"+transfer).addClass('transfer-config-selected');
                jQuery("#" + transfer + "TransferDefinitionPanel").addClass('transfer-config-panel-show').removeClass('transfer-panel');
            },
            // 転送ルール追加
            AddTransferRule(){
                var that = this;
                var transfers = this.transferList.filter(function(item,index){
                    if(item.type_id == that.transferSelected) return true;
                })
                this.tmpTransferTask = jQuery.extend(true,{},this.transferTaskDefault[transfers[0]["name"]]);
                this.tmpTransferTask.config.formId = this.tmpFormInput.hashed_id;
                jQuery('#' + transfers[0]["name"] + 'TransferRuleEditModal').modal('show');
            },
            // 転送ルール編集
            EditTransferRule(index){
                this.tmpTransferTask = jQuery.extend(true,{},this.tmpTransferTaskList[index]);
                jQuery('#' + this.tmpTransferTaskList[index]["transfer_name"] + 'TransferRuleEditModal').modal('show');
            },
            // 転送ルール削除
            DeleteTransferRule(index){
                this.tmpTransferTaskList = this.tmpTransferTaskList.map(function(item, index2){
                    if(index == index2){
                        item.del_flg = 1;
                    }
                    return item;
                })
            },
            // Transfer設定
            SetTransferConfig(type,data){
                this.transferConfig[type] = data.dataset;
            },
            // TransferTask設定
            SetTransferTask(num,rule){
                this.tmpTransferTaskList[num] = jQuery.extend(true,{},rule);
                this.tmpTransferTaskList.push({});
                this.tmpTransferTaskList.pop();
            },
            SetTmpTransferRuleSetting(data){
                for(d in data){
                    console.log(d);
                    this.tmpTransferRuleSetting[d] = data[d];
                }
            }
        },
        computed: {
            orderedItems: function() {
                return _.orderBy(this.formlist, "index")
            },
            orderedUsers: function() {
                return _.orderBy(this.userList, "index")
            },
            orderedCols: function() {
                return _.orderBy(this.tmpFormCols, "index")
            },
            formDataList: function() {
                return this.formPostData.data
            },
            formData: function() {
                var tmp = {};
                if (Object.keys(this.formPostData).length > 0) {
                    tmp.data = this.formPostData.data[this.selectedFormPostDataIdx];
                    tmp.column = this.formPostData.column;
                }
                return tmp;
            },
            formDataColumns: function() {
                return this.formPostData.column
            },
            tmpTransferTaskListFiltered: function(){
                return this.tmpTransferTaskList.filter(function(item,index){
                    if(item.del_flg == 0) return true;
                });
            },
            test: function() {
                return "test";
            }
        },
    })



    jQuery.noConflict();
    jQuery(document).ready(function() {

        jQuery(document).ajaxSend(function(event,jqxhr,settings){
            jqxhr.setRequestHeader('Csrf-Token', jQuery("input[name=csrfToken]").val());
        });
        jQuery(".sfpage").hide();
        jQuery("#formEdit1").hide();
        var reqdata = {
            objtype: "User",
            action: "list",
            rcdata: "iii"
        };

        jQuery.ajax({
            type: "POST",
            url: "rc/",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(reqdata),
            success: function(msg) {
                var tmpArr = {};
                for (d in msg.dataset) {
                    tmpArr = msg.dataset[d];
                    tmpArr["index"] = d;
                    app.userList[d] = tmpArr;
                }

            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
            }
        })
        var reqdata = {
            objtype: "Form",
            action: "list",
            rcdata: "iii"
        };
        jQuery.ajax({
            type: "POST",
            url: "rc/",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(reqdata),
            success: function(msg) {
                app.formlist = JSON.parse(msg.dataset);
                for(var f in app.formlist){
                    app.formlist[f]['index'] = f
                }
                jQuery("#formList").show();
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                console.log(textStatus);
                console.log(errorThrown);
            }
        })

    })
