const DBADMIN_KEY = "hallucination_dbadmin_demo_v1";
const DBADMIN_SCHEMA = {
  app_user: {key:"id", columns:["id","username","email","role","created_at"], readonly:["id","created_at"]},
  saved_query: {key:"id", columns:["id","question","answer","created_at"], readonly:["id","created_at"]},
  ai_claim: {key:"ai_claim_id", columns:["ai_claim_id","ai_claim_text","created_at"], readonly:["ai_claim_id","created_at"]},
  result: {key:"result_id", columns:["result_id","ai_claim_id","verdict","score","created_at"], readonly:["result_id","created_at"]}
};
const DBADMIN_SAMPLE = {
  app_user:[{id:1,username:"demo_user",email:"user@example.com",role:"USER",created_at:"2026-10-18 14:25"},{id:2,username:"demo_admin",email:"admin@example.com",role:"ADMIN",created_at:"2026-10-19 09:10"},{id:3,username:"demo_dbadmin",email:"dbadmin@example.com",role:"DB_ADMIN",created_at:"2026-10-20 11:45"}],
  saved_query:[{id:101,question:"What is the capital of France?",answer:"Paris is the capital of France.",created_at:"2026-10-20 13:15"},{id:102,question:"Who developed the theory of relativity?",answer:"Albert Einstein developed the theory of relativity.",created_at:"2026-10-20 16:40"},{id:103,question:"What is the largest planet?",answer:"Jupiter is the largest planet in our solar system.",created_at:"2026-10-21 08:30"}],
  ai_claim:[{ai_claim_id:201,ai_claim_text:"Paris is the capital of France.",created_at:"2026-10-20 13:15"},{ai_claim_id:202,ai_claim_text:"Jupiter is the largest planet in our solar system.",created_at:"2026-10-21 08:30"}],
  result:[{result_id:301,ai_claim_id:201,verdict:"SUPPORTED",score:0.99,created_at:"2026-10-20 13:16"},{result_id:302,ai_claim_id:202,verdict:"SUPPORTED",score:0.97,created_at:"2026-10-21 08:31"}]
};
let dbadminData = {};
function dbadminClone(data) {return JSON.parse(JSON.stringify(data));}
function dbadminLoad() {try {const saved=JSON.parse(localStorage.getItem(DBADMIN_KEY)); if(saved && Object.keys(DBADMIN_SCHEMA).every(t=>Array.isArray(saved[t])))return saved;}catch(error){console.warn("Demo storage unavailable",error);}return dbadminClone(DBADMIN_SAMPLE);}
function dbadminPersist(){try{localStorage.setItem(DBADMIN_KEY,JSON.stringify(dbadminData));return true;}catch(error){console.warn("Could not save demo records",error);return false;}}
function dbadminElement(id){return document.getElementById(id);}
function dbadminMake(tag,content,cls){const node=document.createElement(tag);if(content!==undefined)node.textContent=String(content);if(cls)node.className=cls;return node;}
function dbadminCurrentTable(){return dbadminElement("dbadmin-table-select")?.value||"saved_query";}
function dbadminFilter(rows){const q=(dbadminElement("dbadmin-search")?.value||"").trim().toLowerCase();return rows.filter(row=>Object.values(row).some(value=>String(value).toLowerCase().includes(q)));}
function dbadminRenderStats(){const target=dbadminElement("dbadmin-stats");if(!target)return;target.replaceChildren();Object.entries(dbadminData).forEach(([table,rows])=>{const box=dbadminMake("div",undefined,"dbadmin-stat");box.append(dbadminMake("span",table),dbadminMake("strong",rows.length));target.append(box);});}
function dbadminRenderTable(table,rows){const target=dbadminElement("dbadmin-table-container");if(!target)return;target.replaceChildren();const columns=DBADMIN_SCHEMA[table].columns;if(!rows.length){target.append(dbadminMake("p","No matching records found.","dbadmin-empty"));return;}const tableNode=dbadminMake("table",undefined,"dbadmin-table");const thead=document.createElement("thead"),head=document.createElement("tr");columns.forEach(col=>head.append(dbadminMake("th",col)));thead.append(head);const tbody=document.createElement("tbody");rows.forEach(record=>{const tr=document.createElement("tr");columns.forEach(col=>{const td=document.createElement("td");if(col==="role"||col==="verdict")td.append(dbadminMake("span",record[col],"dbadmin-pill"));else td.textContent=String(record[col]??"");tr.append(td);});tbody.append(tr);});tableNode.append(thead,tbody);target.append(tableNode);}
function dbadminRenderBrowse(){const table=dbadminCurrentTable();let rows=dbadminFilter(dbadminData[table]);if(document.body.dataset.dbadminPage==="history"){rows=[...rows].sort((a,b)=>(dbadminElement("dbadmin-history-sort")?.value==="oldest"?1:-1)*String(a.created_at).localeCompare(String(b.created_at)));}dbadminRenderTable(table,rows);const count=dbadminElement("dbadmin-row-count");if(count)count.textContent=`Showing ${rows.length} of ${dbadminData[table].length} records`;dbadminRenderStats();}
function dbadminPopulateRecords(){const table=dbadminCurrentTable(),select=dbadminElement("dbadmin-record-select");if(!select)return;select.replaceChildren();dbadminData[table].forEach(row=>{const key=DBADMIN_SCHEMA[table].key;const opt=dbadminMake("option",`${row[key]} — ${row.username||row.question||row.ai_claim_text||row.verdict||"Record"}`);opt.value=String(row[key]);select.append(opt);});dbadminRenderForm();}
function dbadminSelectedRecord(){const table=dbadminCurrentTable(),key=DBADMIN_SCHEMA[table].key,id=dbadminElement("dbadmin-record-select")?.value;return dbadminData[table].find(row=>String(row[key])===id);}
function dbadminRenderForm(){const target=dbadminElement("dbadmin-form-fields");if(!target)return;target.replaceChildren();const table=dbadminCurrentTable(),record=dbadminSelectedRecord();if(!record){target.append(dbadminMake("p","No records available."));return;}DBADMIN_SCHEMA[table].columns.forEach(col=>{const label=dbadminMake("label",undefined,col==="question"||col==="answer"||col==="ai_claim_text"?"dbadmin-field-full":"");const caption=dbadminMake("span",col,"dbadmin-label");label.append(caption);let field;if(col==="answer"||col==="question"||col==="ai_claim_text"){field=dbadminMake("textarea",undefined,"dbadmin-textarea");field.rows=3;field.value=String(record[col]??"");}else if(col==="role"||col==="verdict"){field=dbadminMake("select",undefined,"dbadmin-select");(col==="role"?["USER","ADMIN","DB_ADMIN"]:["SUPPORTED","UNSUPPORTED","UNCERTAIN"]).forEach(value=>{const option=dbadminMake("option",value);option.value=value;field.append(option);});field.value=record[col];}else{field=dbadminMake("input",undefined,"dbadmin-input");field.type=col==="email"?"email":col==="score"?"number":"text";field.value=String(record[col]??"");if(col==="score"){field.min="0";field.max="1";field.step="0.01";}}field.name=col;field.disabled=DBADMIN_SCHEMA[table].readonly.includes(col);if(!field.disabled)field.required=true;label.append(field);target.append(label);});const feedback=dbadminElement("dbadmin-feedback");if(feedback)feedback.textContent="";dbadminRenderTable(table,dbadminData[table]);}
function dbadminSave(event){event.preventDefault();const table=dbadminCurrentTable(),record=dbadminSelectedRecord(),form=dbadminElement("dbadmin-edit-form"),feedback=dbadminElement("dbadmin-feedback");if(!record||!form)return;if(!form.reportValidity())return;const data=new FormData(form);const changes={};for(const col of DBADMIN_SCHEMA[table].columns){if(DBADMIN_SCHEMA[table].readonly.includes(col))continue;const value=String(data.get(col)||"").trim();if(!value){feedback.textContent=`${col} cannot be empty.`;feedback.className="dbadmin-error";return;}if(col==="score"&&(Number(value)<0||Number(value)>1)){feedback.textContent="Score must be between 0 and 1.";feedback.className="dbadmin-error";return;}if(col==="ai_claim_id"&&table==="result"&&!dbadminData.ai_claim.some(row=>String(row.ai_claim_id)===value)){feedback.textContent="Claim ID must match an existing ai_claim record.";feedback.className="dbadmin-error";return;}changes[col]=(col==="score"?Number(value):col==="ai_claim_id"?Number(value):value);}Object.assign(record,changes);const saved=dbadminPersist();feedback.textContent=saved?"Sample record updated successfully.":"Updated in memory, but browser storage is unavailable.";feedback.className=saved?"dbadmin-success":"dbadmin-error";dbadminPopulateRecords();feedback.textContent=saved?"Sample record updated successfully.":"Updated in memory, but browser storage is unavailable.";feedback.className=saved?"dbadmin-success":"dbadmin-error";}
function dbadminDeleteRecord() {
  const table = dbadminCurrentTable();
  const record = dbadminSelectedRecord();
  const saveButton = dbadminElement("dbadmin-save");
  const deleteButton = dbadminElement("dbadmin-delete");

  if (saveButton) saveButton.disabled = !record;
  if (deleteButton) deleteButton.disabled = !record;
  const feedback = dbadminElement("dbadmin-feedback");
  if (!record) return;

  const key = DBADMIN_SCHEMA[table].key;
  const id = record[key];

  // Prevent deleting a claim referenced by a result record.
  if (table === "ai_claim" && dbadminData.result.some(
    result => String(result.ai_claim_id) === String(id)
  )) {
    feedback.textContent = "Cannot delete this claim while a result references it.";
    feedback.className = "dbadmin-error";
    return;
  }

  if (!window.confirm(`Delete ${table} record ${id}? This cannot be undone in the demo unless you reset the sample data.`)) {
    return;
  }

  dbadminData[table] = dbadminData[table].filter(
    row => String(row[key]) !== String(id)
  );

  const saved = dbadminPersist();
  dbadminPopulateRecords();
  if (feedback) {
    feedback.textContent = saved
      ? `Record ${id} deleted from the browser demo data.`
      : `Record ${id} deleted for this session, but browser storage is unavailable.`;
    feedback.className = saved ? "dbadmin-success" : "dbadmin-error";
  }
}

function dbadminClearSearch(){const search=dbadminElement("dbadmin-search");if(search){search.value="";search.focus();}dbadminRenderBrowse();}
function dbadminReset(){if(!window.confirm("Reset all sample records to their original demo values?"))return;dbadminData=dbadminClone(DBADMIN_SAMPLE);dbadminPersist();dbadminRenderBrowse();}
function dbadminSetupBrowse(){dbadminElement("dbadmin-table-select")?.addEventListener("change",dbadminRenderBrowse);dbadminElement("dbadmin-search")?.addEventListener("input",dbadminRenderBrowse);dbadminElement("dbadmin-clear-search")?.addEventListener("click",dbadminClearSearch);dbadminElement("dbadmin-reset-data")?.addEventListener("click",dbadminReset);dbadminElement("dbadmin-history-sort")?.addEventListener("change",dbadminRenderBrowse);dbadminRenderBrowse();}
function dbadminSetupEditor(){dbadminElement("dbadmin-table-select")?.addEventListener("change",dbadminPopulateRecords);dbadminElement("dbadmin-record-select")?.addEventListener("change",dbadminRenderForm);dbadminElement("dbadmin-edit-form")?.addEventListener("submit",dbadminSave);dbadminElement("dbadmin-cancel")?.addEventListener("click",dbadminRenderForm);dbadminElement("dbadmin-delete")?.addEventListener("click",dbadminDeleteRecord);dbadminPopulateRecords();}
function dbadminInitialize(){dbadminData=dbadminLoad();const page=document.body.dataset.dbadminPage;if(page==="history"){const selector=dbadminElement("dbadmin-table-select");if(!selector){const hidden=dbadminMake("select");hidden.id="dbadmin-table-select";hidden.hidden=true;const option=dbadminMake("option","saved_query");option.value="saved_query";hidden.append(option);document.body.append(hidden);}dbadminSetupBrowse();}else if(page==="view")dbadminSetupBrowse();else if(page==="update")dbadminSetupEditor();}
document.addEventListener("DOMContentLoaded",dbadminInitialize);
