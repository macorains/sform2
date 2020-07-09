package net.macolabo.sform2.models.json

import com.sforce.soap.partner._
import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

object PicklistEntry {
  def apply(active :Boolean, defaultValue:Boolean, label:Option[String], value:Option[String]) :PicklistEntry = {
    val picklistEntry = new PicklistEntry
    picklistEntry.setActive(active)
    picklistEntry.setDefaultValue(defaultValue)
    picklistEntry.setLabel(label.getOrElse(""))
    picklistEntry.setValue(value.getOrElse(""))
    picklistEntry
  }
}

object Field {
  def apply(autonumber:Boolean, byteLength:Int, caseSensitive:Boolean, createable:Boolean, defaultedOnCreate:Boolean,
            defaultValueFormula:Option[String], dependentPicklist:Boolean, digits:Int, extraTypeInfo:Option[String], label:String,
            length:Int, name:String, nillable:Boolean, picklistValues:List[PicklistEntry], precision:Int,
            referenceTargetField:Option[String], restrictedPicklist:Boolean, scale:Int, field_type:String, unique:Boolean,
            updatable:Boolean, writeRequiresMasterRead:Boolean
           ): Field = {
    val field = new Field()
    field.setAutoNumber(autonumber)
    field.setByteLength(byteLength)
    field.setCaseSensitive(caseSensitive)
    field.setCreateable(createable)
    field.setDefaultedOnCreate(defaultedOnCreate)
    field.setDefaultValueFormula(defaultValueFormula.getOrElse(""))
    field.setDependentPicklist(dependentPicklist)
    field.setDigits(digits)
    field.setExtraTypeInfo(extraTypeInfo.getOrElse(""))
    field.setLabel(label)
    field.setLength(length)
    field.setName(name)
    field.setNillable(nillable)
    field.setPicklistValues(picklistValues.toArray)
    field.setPrecision(precision)
    field.setReferenceTargetField(referenceTargetField.getOrElse(""))
    field.setRestrictedPicklist(restrictedPicklist)
    field.setScale(scale)
    field.setType(FieldType.valueOf(field_type.replace("boolean","_boolean").replace("int","_int").replace("double","_double")))
    field.setUnique(unique)
    field.setUpdateable(updatable)
    field.setWriteRequiresMasterRead(writeRequiresMasterRead)
    field
  }
}

object DescribeSObjectResult {
  def apply(activateable: Boolean, compactLayoutable: Boolean, createable: Boolean, custom: Boolean, customSetting: Boolean,
              deletable: Boolean, deprecatedAndHidden: Boolean, feedEnabled: Boolean, fields:List[Field], keyPrefix:String,
              label: String,labelPlural: String, mergeable: Boolean, mruEnabled: Boolean, name:String, queryable:Boolean,
              replicateable: Boolean, retrieveable: Boolean, searchable: Boolean, searchLayoutable: Boolean,
              undeletable: Boolean, updateable: Boolean) :DescribeSObjectResult= {
    val describeSObjectResult = new DescribeSObjectResult()
    describeSObjectResult.setActivateable(activateable)
    describeSObjectResult.setCompactLayoutable(compactLayoutable)
    describeSObjectResult.setCreateable(createable)
    describeSObjectResult.setCustom(custom)
    describeSObjectResult.setCustomSetting(customSetting)
    describeSObjectResult.setDeletable(deletable)
    describeSObjectResult.setDeprecatedAndHidden(deprecatedAndHidden)
    describeSObjectResult.setFeedEnabled(feedEnabled)
    describeSObjectResult.setFields(fields.toArray.asInstanceOf[Array[IField]])
    describeSObjectResult.setKeyPrefix(keyPrefix)
    describeSObjectResult.setLabel(label)
    describeSObjectResult.setLabelPlural(labelPlural)
    describeSObjectResult.setMergeable(mergeable)
    describeSObjectResult.setMruEnabled(mruEnabled)
    describeSObjectResult.setName(name)
    describeSObjectResult.setQueryable(queryable)
    describeSObjectResult.setReplicateable(replicateable)
    describeSObjectResult.setRetrieveable(retrieveable)
    describeSObjectResult.setSearchable(searchable)
    describeSObjectResult.setSearchLayoutable(searchLayoutable)
    describeSObjectResult.setUndeletable(undeletable)
    describeSObjectResult.setUpdateable(updateable)
    describeSObjectResult
  }
}


trait SalesforceTransferJson {


  implicit val SFPicklistEntryWrites = new Writes[PicklistEntry] {
    def writes(picklistEntry: PicklistEntry) = Json.obj(
      "active" -> picklistEntry.getActive,
      //"validFor" -> picklistEntry.getValidFor,
      "defaultValue" -> picklistEntry.getDefaultValue,
      "label" -> picklistEntry.getLabel,
      "value" -> picklistEntry.getValue
    )
  }

  implicit val SFPicklistEntryReads = (
      (JsPath \ "active").read[Boolean] ~
      (JsPath \ "defaultValue").read[Boolean] ~
      (JsPath \ "label").readNullable[String] ~
      (JsPath \ "value").readNullable[String]
  )(PicklistEntry.apply _)

  implicit val SFFieldWrites = new Writes[Field] {
    def writes(field: Field) = Json.obj(
      "autonumber" -> field.getAutoNumber,
      "byteLength" -> field.getByteLength,
      //"calculated" -> field.getCalculated,
      "caseSensitive" -> field.getCalculated,
      //"controllerName" -> field.getControllerName,
      "createable" -> field.getCreateable,
      //"custom" -> field.getCustom,
      "defaultedOnCreate" -> field.getDefaultedOnCreate,
      "defaultValueFormula" -> field.getDefaultValueFormula,
      "dependentPicklist" -> field.getDependentPicklist,
      //"deprecatedAndHidden" -> field.getDeprecatedAndHidden,
      "digits" -> field.getDigits,
      //"encrypted" -> field.getEncrypted,
      "extraTypeInfo" -> field.getExtraTypeInfo,
      //"filterable" -> field.getFilterable,
      //"filteredLookupInfo" -> field.getFilteredLookupInfo,
      //"formula" -> field.getFormula,
      //"groupable" -> field.getGroupable,
      //"highScaleNumber" -> field.getHighScaleNumber,
      //"htmlFormatted" -> field.getHtmlFormatted,
      //"idLookup" -> field.getIdLookup,
      //"inlineHelpText" -> field.getInlineHelpText,
      "label" -> field.getLabel,
      "length" -> field.getLength,
      //"mask" -> field.getMask,
      //"maskType" -> field.getMaskType,
      "name" -> field.getName,
      //"nameField" -> field.getNameField,
      //"namePointing" -> field.getNamePointing,
      "nillable" -> field.getNillable,
      //"permissionable" -> field.getPermissionable,
      "picklistValues" -> field.getPicklistValues,
      //"polymorphicForeignKey" -> field.getPolymorphicForeignKey,
      "precision" -> field.getPrecision,
      //"relationshipName" -> field.getRelationshipName,
      //"relationshipOrder" -> field.getRelationshipOrder,
      "referenceTargetField" -> field.getReferenceTargetField,
      //"referenceTo" -> field.getReferenceTo,
      "restrictedPicklist" -> field.getRestrictedPicklist,
      "scale" -> field.getScale,
      //"searchPrefilterable" -> field.getSearchPrefilterable,
      //"soapType" -> field.getSoapType.toString,
      //"sortable" -> field.getSortable,
      "type" -> field.getType.toString,
      "unique" -> field.getUnique,
      "updateable" -> field.getUpdateable,
      "writeRequiresMasterRead" -> field.getWriteRequiresMasterRead
    )
  }

  implicit val SFFieldReads = (
      (JsPath \ "autonumber").read[Boolean] ~
      (JsPath \ "byteLength").read[Int] ~
      //(JsPath \ "calculated").read[Boolean] ~
      (JsPath \ "caseSensitive").read[Boolean] ~
      //(JsPath \ "controllerName").read[String] ~
      (JsPath \ "createable").read[Boolean] ~
      //(JsPath \ "custom").read[Boolean] ~
      (JsPath \ "defaultedOnCreate").read[Boolean] ~
      (JsPath \ "defaultValueFormula").readNullable[String] ~
      (JsPath \ "dependentPicklist").read[Boolean] ~
      //(JsPath \ "deprecatedAndHidden").read[Boolean] ~
      (JsPath \ "digits").read[Int] ~
      //(JsPath \ "encrypted").read[Boolean] ~
      (JsPath \ "extraTypeInfo").readNullable[String] ~
      //(JsPath \ "filterable").read[Boolean] ~
      //(JsPath \ "filteredLookupInfo").read[FilteredLookupInfo] ~
      //(JsPath \ "formula").read[String] ~
      //(JsPath \ "groupable").read[Boolean] ~
      //(JsPath \ "highScaleNumber").read[Boolean] ~
      //(JsPath \ "htmlFormatted").read[Boolean] ~
      //(JsPath \ "idLookup").read[Boolean] ~
      //(JsPath \ "inlineHelpText").read[String] ~
      (JsPath \ "label").read[String] ~
      (JsPath \ "length").read[Int] ~
      //(JsPath \ "mask").read[String] ~
      //(JsPath \ "maskType").read[String] ~
      (JsPath \ "name").read[String] ~
      //(JsPath \ "nameField").read[Boolean] ~
      //(JsPath \ "namePointing").read[Boolean] ~
      (JsPath \ "nillable").read[Boolean] ~
      //(JsPath \ "permissionable").read[Boolean] ~
      (JsPath \ "picklistValues").read[List[PicklistEntry]] ~
      //(JsPath \ "polymorphicForeignKey").read[Boolean] ~
      (JsPath \ "precision").read[Int] ~
      //(JsPath \ "relationshipName").read[String] ~
      //(JsPath \ "relationshipOrder").read[Int] ~
      (JsPath \ "referenceTargetField").readNullable[String] ~
      //(JsPath \ "referenceTo").read[List[String]] ~
      (JsPath \ "restrictedPicklist").read[Boolean] ~
      (JsPath \ "scale").read[Int] ~
      //(JsPath \ "searchPrefilterable").read[Boolean] ~
      //(JsPath \ "soapType").read[SOAPType] ~
      //(JsPath \ "sortable").read[Boolean] ~
      (JsPath \ "type").read[String] ~
      (JsPath \ "unique").read[Boolean] ~
      (JsPath \ "updateable").read[Boolean] ~
      (JsPath \ "writeRequiresMasterRead").read[Boolean]
  )(Field.apply _)

  implicit val SFDescribeSObjectResultWrites = new Writes[DescribeSObjectResult] {
    def writes(describeSObjectResult: DescribeSObjectResult) = Json.obj(
      "activateable" -> describeSObjectResult.getActivateable,
      //"actionOverrides" -> describeSObjectResult.getActionOverrides,
      //"childRelationships" -> describeSObjectResult.getChildRelationships,
      "compactLayoutable" -> describeSObjectResult.getCompactLayoutable,
      "createable" -> describeSObjectResult.getCreateable,
      "custom" -> describeSObjectResult.getCustom,
      "customSetting" -> describeSObjectResult.getCustomSetting,
      "deletable" -> describeSObjectResult.getDeletable,
      "deprecatedAndHidden" -> describeSObjectResult.getDeprecatedAndHidden,
      "feedEnabled" -> describeSObjectResult.getFeedEnabled,
      "fields" -> describeSObjectResult.getFields,
      "keyPrefix" -> describeSObjectResult.getKeyPrefix,
      "label" -> describeSObjectResult.getLabel,
      "labelPlural" -> describeSObjectResult.getLabelPlural,
      //"layoutable" -> describeSObjectResult.getCompactLayoutable,
      "mergeable" -> describeSObjectResult.getMergeable,
      "mruEnabled" -> describeSObjectResult.getMruEnabled,
      "name" -> describeSObjectResult.getName,
      //"namedLayoutInfos" -> describeSObjectResult.getNamedLayoutInfos,
      //"networkScopeFieldName" -> describeSObjectResult.getNetworkScopeFieldName,
      "queryable" -> describeSObjectResult.getQueryable,
      //"recordTypeInfos" -> describeSObjectResult.getRecordTypeInfos,
      "replicateable" -> describeSObjectResult.getReplicateable,
      "retrieveable" -> describeSObjectResult.getRetrieveable,
      "searchable" -> describeSObjectResult.getSearchable,
      "searchLayoutable" -> describeSObjectResult.getSearchLayoutable,
      //"supportedScopes" -> describeSObjectResult.getSupportedScopes,
      //"triggerable" -> describeSObjectResult.getTriggerable,
      "undeletable" -> describeSObjectResult.getDeletable,
      "updateable" -> describeSObjectResult.getUpdateable,
      //"urlDetail" -> describeSObjectResult.getUrlDetail,
      //"urlEdit" -> describeSObjectResult.getUrlEdit,
      //"urlNew" -> describeSObjectResult.getUrlNew
    )
  }

  implicit val SFDescribeSObjectResultReads = (
    (JsPath \ "activateable").read[Boolean] ~

    //(JsPath \ "actionOverrides").read[] ~
    //(JsPath \ "childRelationships").read[] ~
    (JsPath \ "compactLayoutable").read[Boolean] ~
    (JsPath \ "createable").read[Boolean] ~
    (JsPath \ "custom").read[Boolean] ~
    (JsPath \ "customSetting").read[Boolean] ~
    (JsPath \ "deletable").read[Boolean] ~
    (JsPath \ "deprecatedAndHidden").read[Boolean] ~
    (JsPath \ "feedEnabled").read[Boolean] ~
    (JsPath \ "fields").read[List[Field]] ~
    (JsPath \ "keyPrefix").read[String] ~
    (JsPath \ "label").read[String] ~
    (JsPath \ "labelPlural").read[String] ~
    //(JsPath \ "layoutable").read[Boolean] ~
    (JsPath \ "mergeable").read[Boolean] ~
    (JsPath \ "mruEnabled").read[Boolean] ~
    (JsPath \ "name").read[String] ~
    //(JsPath \ "namedLayoutInfos").read[] ~
    //(JsPath \ "networkScopeFieldName").read[String] ~
    (JsPath \ "queryable").read[Boolean] ~
    //(JsPath \ "recordTypeInfos").read[] ~
    (JsPath \ "replicateable").read[Boolean] ~
    (JsPath \ "retrieveable").read[Boolean] ~
    (JsPath \ "searchable").read[Boolean] ~
    (JsPath \ "searchLayoutable").read[Boolean] ~
    // (JsPath \ "supportedScopes").read[] ~
    //(JsPath \ "triggerable").read[Boolean] ~
    (JsPath \ "undeletable").read[Boolean] ~
    (JsPath \ "updateable").read[Boolean]
    //(JsPath \ "urlDetail").read[String] ~
    //(JsPath \ "urlEdit").read[String] ~
    //(JsPath \ "urlNew").read[String]
  )(DescribeSObjectResult.apply _)

}
