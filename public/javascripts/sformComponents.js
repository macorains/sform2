var salesforceTransferConfigComponentTemplate = (function () {/*
<div class="row transfer-panel" id="SalesforceTransferDefinitionPanel">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">設定</h3>
        </div>
        <div class="panel-body">
            <div class="row">
                <div class="col-md-4">Salesforceユーザ名</div>
                <div class="col-md-8">{{transferConfig.Salesforce.user}}</div>
            </div>
            <div class="row">
                <div class="col-md-4">Salesforceパスワード</div>
                <div class="col-md-8">----------</div>
            </div>
            <div class="row">
                <div class="col-md-4">Salesforceセキュリティトークン</div>
                <div class="col-md-8">----------</div>
            </div>
            <div class="row">
                <div class="col-md-12 text-right">
                    <button type="button" class="btn btn-primary" id="changeSalesforceSetting" v-on:click="SalesforceTransferConfigEditModalShow()">変更</button>
                </div>
            </div>
        </div>
    </div>
    <div class="modal fade" tabindex="-1" role="dialog" id="SalesforceTransferConfigEditModal" style="display:none">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">Salesforce転送設定</h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-5">Salesforceユーザ名</div>
                        <div class="col-md-7"><input type="text" class="form-control" name="salesforce-transfer-config-username" v-model="transferConfig.Salesforce.user"></div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">Salesforceパスワード</div>
                        <div class="col-md-7"><input type="text" class="form-control" name="salesforce-transfer-config-password" v-model="transferConfig.Salesforce.password"></div>
                    </div>
                    <div class="row">
                        <div class="col-md-5">Salesforceセキュリティトークン</div>
                        <div class="col-md-7"><input type="text" class="form-control" name="salesforce-transfer-config-security-token" v-model="transferConfig.Salesforce.securityToken"></div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">閉じる</button>
                    <button type="button" class="btn btn-primary" v-on:click="saveSalesforceTransferConfig">保存</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
</div>
*/}).toString().replace(/(\n)/g, '').split('*')[1];

var salesforceTransferConfigComponent = {
  props: ['transferConfig','transferTaskDefault'],
  created : function(){
    var that = this;
    // SalesforceTransferの設定情報取得
    var reqdata = {
        objtype: "Transfer",
        action: "getConfig",
        rcdata: {
            transferName : "Salesforce"
        }
    };
    jQuery(document).ajaxSend(function(event,jqxhr,settings){
        jqxhr.setRequestHeader('Csrf-Token', jQuery("input[name=csrfToken]").val());
    });
    jQuery.ajax({
        type: "POST",
        url: "rc/",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(reqdata),
        success: function(msg) {
        console.log(msg);
            that.$emit('set','Salesforce',msg);
            that.transferTaskDefault.Salesforce = {
                status : 1,
                transfer_name : "Salesforce",
                transfer_type_id : 1,
                config : {
                    columnConvertDefinition : [],
                    sfObject : msg.dataset.sfObjectDefinition[0]["name"]
                }
            }
        }
    });
  },
  template: salesforceTransferConfigComponentTemplate,
  methods: {
    SalesforceTransferConfigEditModalShow() {
        jQuery("#SalesforceTransferConfigEditModal").modal('show');
    },
    saveSalesforceTransferConfig(){
        var reqdata = {
            objtype: "Transfer",
            action: "saveConfig",
            rcdata: {
                transferName : "Salesforce",
                config : this.transferConfig.Salesforce
            }
        };
        jQuery.ajax({
            type: "POST",
            url: "rc/",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(reqdata),
            success: function(msg) {
                console.log(msg);
            }
        });
        jQuery("#SalesforceTransferConfigEditModal").modal('hide');
    }
  }
};

var salesforceTransferRuleEditComponentTemplate = (function () {/*
<div class="modal fade" tabindex="-1" role="dialog" id="SalesforceTransferRuleEditModal" style="display:none">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Salesforce転送ルール設定</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                  <div class="col-md-4">転送ルール名</div>
                  <div class="col-md-8"><input type="text" class="form-control" v-model="tmpTransferTask.name"></div>
                </div>
                <div class="row">
                  <div class="col-md-4">Salesforceオブジェクト</div>
                  <div class="col-md-8">
                    <select class="form-control" id="SalesforceTransferRuleEditModal_sfObject" v-on:click="beforeChangeSalesforceObject" v-on:change="changeSalesforceObject" v-model="tmpTransferTask.config.sfObject" v-el:sfobject>
                      <option v-for="sfObject in transferConfig.Salesforce.sfObjectDefinition" value="{{sfObject.name}}">{{sfObject.label}}</option>
                    </select>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-12">&nbsp;</div>
                </div>
                <div class="row">
                  <div class="col-md-12">項目割付</div>
                </div>
                <div class="row">
                  <div class="col-md-12">
                    <table class="table">
                      <thead>
                        <tr><th>SForm項目</th><th>Salesforce項目</th><th></th></tr>
                      </thead>
                      <tbody>
                        <tr v-for="(index,c) in SalesforceColumnConvertDefinitions">
                          <td>{{c.sformCol}}</td>
                          <td>{{c.sfCol}}</td>
                          <td>
                            <button class="btn btn-default" v-on:click="deleteSalesforceConverrtDefinition(index)">
                            削除</button>
                          </td>
                        </tr>
                      </tbody>
                    </table>
                  </div>
                </div>
                <div class="row">
                  <div class="col-md-5">
                    <select class="form-control" v-model="tmpTransferRuleSetting.sformCol">
                      <option v-for="col in formColList" value="{{col.colId}}">{{col.name}}</option>
                    </select>
                  </div>
                  <div class="col-md-5">
                    <select class="form-control" v-model="tmpTransferRuleSetting.sfCol">
                      <option v-for="sfcol in sfColList" value="{{sfcol.name}}">{{sfcol.label}}</option>
                    </select>
                  </div>
                  <div class="col-md-2">
                    <button type="button" class="btn btn-primary" v-on:click="addSalesforceColumnConvertDefinitions">追加</button>
                  </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">閉じる</button>
                <button type="button" class="btn btn-primary" v-on:click="saveSalesforceTransferRule">保存</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
*/}).toString().replace(/(\n)/g, '').split('*')[1];

var salesforceTransferRuleEditComponent = {
    props: ['transferConfig','formCols','tmpTransferTask','tmpTransferTaskList','transferTaskDefault','tmpTransferRuleSetting','tmpFormInput','tmpSFObject'],
    created: function(){
        //this.tmpTransferRuleSetting = {sformCol:"", sfCol:"", sformColConvert:{}, sfColConvert:{}};

    },
    template: salesforceTransferRuleEditComponentTemplate,
    methods : {
        beforeChangeSalesforceObject(){
            var that = this;
            this.tmpSFObject = function(){
                if(typeof that.tmpTransferTask.config != 'undefined' && typeof that.tmpTransferTask.config.sfObject != 'undefined'){
                    return that.tmpTransferTask.config.sfObject;
                }
                return ''
            }
        },
        changeSalesforceObject(){
            var res = confirm("オブジェクトを変更すると項目割付が全て削除されます");
            if(!res){
              this.tmpTransferTask.config.sfObject = this.tmpSFObject;
              this.$els.sfobject.value = this.tmpSFObject;
            } else {
              this.tmpTransferTask.config.columnConvertDefinition = [];
            }
        },
        addSalesforceColumnConvertDefinitions(){
            this.tmpTransferTask.config.columnConvertDefinition.push({
                sformCol : this.tmpTransferRuleSetting.sformCol,
                sfCol : this.tmpTransferRuleSetting.sfCol
            });
        },
        saveSalesforceTransferRule(){
            if(typeof this.tmpTransferTask.id == 'undefined'){
                this.tmpTransferTask.id = 0;
                this.tmpTransferTask.newNum = this.tmpTransferTaskList.length;
                this.tmpTransferTaskList.push(this.tmpTransferTask);
            } else {
                for(t in this.tmpTransferTaskList){
                    if(this.tmpTransferTask.id == 0){
                        if(this.tmpTransferTaskList[t].newNum == this.tmpTransferTask.newNum){
                            this.$emit('set',t,this.tmpTransferTask);
                        }
                    } else {
                        if(this.tmpTransferTaskList[t].id == this.tmpTransferTask.id){
                            this.$emit('set',t,this.tmpTransferTask);
                        }
                    }
                }
            }
            jQuery("#SalesforceTransferRuleEditModal").modal('hide');
        },
        deleteSalesforceConverrtDefinition(index) {
            this.tmpTransferTask.config.columnConvertDefinition.splice(index,1);
        }
    },
    computed : {
          SalesforceColumnConvertDefinitions(){
            var that = this;
            var tmp = {}

            if(typeof this.tmpTransferTask.config != 'undefined'){
                var def = this.tmpTransferTask.config.columnConvertDefinition;
                for(var d in def){
                    tmp[d] = {};
                    for(col in this.tmpFormInput.formCols){
                        if(this.tmpFormInput.formCols[col]['colId'] == this.tmpTransferTask.config.columnConvertDefinition[d].sformCol){
                            tmp[d]['sformCol'] = this.tmpFormInput.formCols[col]['name'];
                        }
                    }
                    var that = this;
                    var sfObjectName = '';

                    if(typeof this.tmpTransferTask.config !='undefined' && typeof this.tmpTransferTask.config.sfObject != 'undefined'){
                        sfObjectName = this.tmpTransferTask.config.sfObject;
                    }

                    /*
                    var sfObjectName = function(){
                        if(typeof that.tmpTransferTask.config !='undefined' && typeof that.tmpTransferTask.config.sfObject != 'undefined'){
                            return that.tmpTransferTask.config.sfObject;
                        }
                        return '';
                    }
                    */

                    var sfObjectDefinisions = [];
                    for(s in this.transferConfig.Salesforce.sfObjectDefinition){
                        if(this.transferConfig.Salesforce.sfObjectDefinition[s].name == sfObjectName){
                            sfObjectDefinisions.push(this.transferConfig.Salesforce.sfObjectDefinition[s]);
                        }
                    }
                    /*
                    var sfObjectDefinisions = this.transferConfig.Salesforce.sfObjectDefinition.filter(function(item,index){
                        if(item.name == sfObjectName) return true;
                    });
                    */

                    if(sfObjectDefinisions.length>0){
                        var sfLabel = "";
                        for(s in sfObjectDefinisions[0]['fields']){
                            if(sfObjectDefinisions[0]['fields'][s]['name'] == this.tmpTransferTask.config.columnConvertDefinition[d]['sfCol']){
                                sfLabel = sfObjectDefinisions[0]['fields'][s]['label'];
                            }
                        }
                        /*
                        var sfObjectFields = sfObjectDefinisions[0]['fields'].filter(function(item,index){
                            if(item.name == that.tmpTransferTask.config.columnConvertDefinition[d].sfCol) return true;
                        });
                        if(sfObjectFields.length>0){
                            tmp[d]['sfCol'] = sfObjectFields[0]['label'];
                        }
                        */
                        tmp[d]['sfCol'] = sfLabel;
                    }
                }
            }
            return tmp;
          },
          formColList(){
            var that = this;
            var tmp = {};
            for(col in this.tmpFormInput.formCols){
                if(typeof this.tmpTransferTask.config != 'undefined'
                        && typeof this.tmpTransferTask.config.columnConvertDefinition != 'undefined'){
                    var d = this.tmpTransferTask.config.columnConvertDefinition.filter(function(item,index){
                        if(that.tmpFormInput.formCols[col].colId == item.sformCol) return true;
                    })
                    if(d.length==0) tmp[col] = this.tmpFormInput.formCols[col];
                }
            }
            return tmp;
          },
          sfColList(){
            var that = this;
            var sfObjectFields = {};
            if(typeof this.tmpTransferTask.config != 'undefined'){
                var that = this;

                var sfObjectName = '';
                if(typeof this.tmpTransferTask.config != 'undefined'&& typeof this.tmpTransferTask.config.sfObject != 'undefined'){
                    sfObjectName = this.tmpTransferTask.config.sfObject
                }

                var sfObjectDefinisions = [];
                for(var d in this.transferConfig.Salesforce.sfObjectDefinition){
                    if(this.transferConfig.Salesforce.sfObjectDefinition[d].name == sfObjectName){
                        sfObjectDefinisions.push(this.transferConfig.Salesforce.sfObjectDefinition[d]);
                    }
                }

                if(sfObjectDefinisions.length>0){
                    sfObjectFields = sfObjectDefinisions[0]['fields'].filter(function(item,index){
                        var res = true;
                        for(var d in that.tmpTransferTask.config.columnConvertDefinition){
                            if(that.tmpTransferTask.config.columnConvertDefinition[d].sfCol == item.name){
                                res = false;
                            }
                        }
                        return res;
                    });
                    return sfObjectFields;
                }
            }
          }
    }
};

var mailTransferConfigComponentTemplate = (function () {/*
<div class="row transfer-panel" id="MailTransferDefinitionPanel">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">設定</h3>
        </div>
        <div class="panel-body">
            <div class="row">
                <div class="col-md-4">アドレス帳</div>
                <div class="col-md-8">
                    <table class="table">
                      <thead>
                        <tr><th>名称</th><th>メールアドレス</th></tr>
                      </thead>
                      <tbody>
                        <tr><td>テスト</td><td>test@test.test</td></tr>
                      </tbody>
                    </table>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12 text-right">
                    <button type="button" class="btn btn-primary" id="changeSalesforceSetting" v-on:click="MailTransferConfigEditModalShow()">変更</button>
                </div>
            </div>
        </div>
    </div>
    <div class="modal fade" tabindex="-1" role="dialog" id="MailTransferConfigEditModal" style="display:none">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">Mail転送設定</h4>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-3">アドレス帳</div>
                        <div class="col-md-9">
                            <table class="table">
                              <thead>
                                <tr><th>名称</th><th>メールアドレス</th><th></th></tr>
                              </thead>
                              <tbody>
                                <tr>
                                  <td><input type="text" class="form-control" value="テスト"></td>
                                  <td><input type="text" class="form-control" value="test@test.test"></td>
                                  <td><button class="btn btn-default" type="button">削除</button></td>
                                </tr>
                              </tbody>
                            </table>

                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">閉じる</button>
                    <button type="button" class="btn btn-primary" v-on:click="saveMailTransferConfig">保存</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div><!-- /.modal -->
</div>
*/}).toString().replace(/(\n)/g, '').split('*')[1];

var mailTransferConfigComponent = {
  props: ['transferConfig','transferTaskDefault'],
  created : function(){


    var that = this;
    // MailTransferの設定情報取得
    var reqdata = {
        objtype: "Transfer",
        action: "getConfig",
        rcdata: {
            transferName : "Mail"
        }
    };
    jQuery(document).ajaxSend(function(event,jqxhr,settings){
        jqxhr.setRequestHeader('Csrf-Token', jQuery("input[name=csrfToken]").val());
    });
    jQuery.ajax({
        type: "POST",
        url: "rc/",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(reqdata),
        success: function(msg) {
            console.log(msg);
            that.$emit('set','Mail',msg);
            that.transferTaskDefault.Mail = {
                status : 1,
                transfer_name : "Mail",
                transfer_type_id : 2,
                name : "",
                config : {
                    mailSubject : "",
                    mailFrom : "",
                    mailTo : "",
                    mailBody : ""
                }
            };
        }
    });
  },
  template: mailTransferConfigComponentTemplate,
  methods : {
    MailTransferConfigEditModalShow : function(){
      jQuery("#MailTransferConfigEditModal").modal('show');
    },
    saveMailTransferConfig : function(){}
  },
  computed : {}
};

var mailTransferRuleEditComponentTemplate = (function () {/*
<div class="modal fade" tabindex="-1" role="dialog" id="MailTransferRuleEditModal" style="display:none">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">Mail転送ルール設定</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                  <div class="col-md-4">転送ルール名</div>
                  <div class="col-md-8"><input type="text" class="form-control" v-model="tmpTransferTask.name"></div>
                </div>
                <div class="row">
                  <div class="col-md-4">メール件名</div>
                  <div class="col-md-8"><input type="text" class="form-control" v-model="tmpTransferTask.config.mailSubject"></div>
                </div>
                <div class="row">
                  <div class="col-md-4">メール送信元</div>
                  <div class="col-md-8"><input type="text" class="form-control" v-model="tmpTransferTask.config.mailFrom"></div>
                </div>
                <div class="row">
                  <div class="col-md-4">メール送信先</div>
                  <div class="col-md-8"><input type="text" class="form-control" v-model="tmpTransferTask.config.mailTo"></div>
                </div>
                <div class="row">
                  <div class="col-md-4">メール本文</div>
                  <div class="col-md-8"><textarea class="form-control" v-model="tmpTransferTask.config.mailBody"></textarea></div>
                </div>
                <div class="row">
                  <div class="col-md-12">&nbsp;</div>
                </div>
                <div class="row">
                  <div class="col-md-12">
                    <div class="panel panel-default">
                      <div class="panel-body">
                        <div class="row">
                          <div class="col-md-12">
                            フォーム埋込タグ取得
                          </div>
                        </div>
                        <div class="row">
                          <div class="col-md-6">
                            <select class="form-control" v-model="tmpColSelectId">
                              <option v-for="col in tmpFormInput.formCols" value="{{col.colId}}">{{col.name}}</option>
                            </select>
                          </div>
                          <div class="col-md-6">
                            <input class="form-control" type="text" value="{{tmpColSelectIdTag}}" v-el:tmp-col-tag>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">閉じる</button>
                <button type="button" class="btn btn-primary" v-on:click="saveMailTransferRule">保存</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

*/}).toString().replace(/(\n)/g, '').split('*')[1];

var mailTransferRuleEditComponent = {
  props: ['transferConfig','transferTaskDefault','tmpTransferTaskList','tmpTransferTask','tmpFormInput','tmpColSelectId'],
  created : function(){
  },
  template: mailTransferRuleEditComponentTemplate,
  methods : {
        saveMailTransferRule(){
            if(typeof this.tmpTransferTask.id == 'undefined'){
                this.tmpTransferTask.id = 0;
                this.tmpTransferTask.newNum = this.tmpTransferTaskList.length;
                this.tmpTransferTaskList.push(this.tmpTransferTask);
            } else {
                for(t in this.tmpTransferTaskList){
                    if(this.tmpTransferTask.id == 0){
                        if(this.tmpTransferTaskList[t].newNum == this.tmpTransferTask.newNum){
                            this.$emit('set',t,this.tmpTransferTask);
                        }
                    } else {
                        if(this.tmpTransferTaskList[t].id == this.tmpTransferTask.id){
                            this.$emit('set',t,this.tmpTransferTask);
                        }
                    }
                }
            }
            jQuery("#MailTransferRuleEditModal").modal('hide');
        }
  },
  computed : {
    formColList(){

    },
    tmpColSelectIdTag(){
        return typeof this.tmpColSelectId == 'undefined'?'':'{%' + this.tmpColSelectId + '%}';
    }
  }
};



var sformComponents = {
    'mail-tansfer-config' : mailTransferConfigComponent,
    'mail-tansfer-rule-edit' : mailTransferRuleEditComponent,
    'salesforce-tansfer-config' : salesforceTransferConfigComponent,
    'salesforce-tansfer-rule-edit' : salesforceTransferRuleEditComponent
};

