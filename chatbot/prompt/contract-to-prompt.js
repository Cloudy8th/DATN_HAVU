import { CONTRACT } from "../db/contract.js";

export function contractToPrompt() {
  let out = "SCHEMA (chỉ các resource/field được phép):\n\n";
  for (const [resName, res] of Object.entries(CONTRACT.resources)) {
    out += `RESOURCE ${resName} -> ${res.view}\n`;
    out += `Description: ${res.description}\nFIELDS:\n`;
    for (const [f, meta] of Object.entries(res.fields)) {
      out += `  - ${f} (${meta.type}): ${meta.description}\n`;
    }
    out += `Default Select: ${
      (res.defaultSelect || []).join(", ") || "(none)"
    }\n`;
    out += `Filterable: ${res.filterable ? "yes" : "no"} | Sortable: ${
      res.sortable ? "yes" : "no"
    }\n\n`;
  }
  out += "Trả về JSON Query Plan, không sinh SQL.\n";
  return out;
}
