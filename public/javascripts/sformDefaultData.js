    var sfdata = {
         sfobj: {},
         formlist: {},
         userList: {},
         transferConfig: {
            Salesforce : {
                user : "",
                password : "",
                securityToken : ""
            }
         },
         transferList : [],
         transferSelected : 0,
         transferTaskDefault : {},
         tmpUserInput: {},
         tmpFormInput: {},
         tmpFormCols: {},
         tmpFormColInput: {
            validations : {
                inputType : 0,
                minLength : 0,
                maxLength : 0
            }
         },
         tmpFormColSelect: {},
         tmpFormColSelectBackup: {},
         tmpColumnConvertDefinition : {},
         tmpTransferSetting: {},
         tmpTransferTaskList: [],
         tmpTransferTask: {},
         tmpTransferRuleSetting: {},
         isChange: false,
         inColEdit: false,
         deleteId: null,
         deleteIdx: null,
         formPostData: {},
         selectedFormPostDataIdx: 0,
         menuStatus: {
             0: "active",
             1: "",
             2: "",
             3: ""
         },
         tmpFormDataTable: {},
     };
