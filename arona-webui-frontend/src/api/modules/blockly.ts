import { BlocklyProject } from "@/interface/modules/blockly";
import service from "@/api/http";

export function fetchBlocklyProjectList() {
  return service.raw<BlocklyProject[]>({
    url: "/blockly/commit",
    method: "GET",
  });
}

interface BlocklyProjectSaveData {
  trigger: string;
  uuid: string | null;
  projectName: string;
  blocklyProject: string;
  userData: UserData | string;
}

export interface UserData {
  members: { groupId: number }[];
}

export function saveBlocklyProject(data: BlocklyProjectSaveData) {
  return service.raw<null>({
    url: "/blockly/commit",
    method: "POST",
    data: {
      mode: "CREATE",
      ...data,
    },
  });
}

export function updateBlocklyProject(data: BlocklyProjectSaveData) {
  return service.raw<null>({
    url: "/blockly/commit",
    method: "POST",
    data: {
      mode: "UPDATE",
      ...data,
    },
  });
}

export function deleteBlocklyProject(data: BlocklyProjectSaveData) {
  return service.raw<null>({
    url: "/blockly/commit",
    method: "POST",
    data: {
      mode: "DELETE",
      ...data,
    },
  });
}
