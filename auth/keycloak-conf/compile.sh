#!/bin/sh

# source: https://e.printstacktrace.blog/merging-json-files-recursively-in-the-command-line/
SCRIPT='def deepmerge(a;b):
  reduce b[] as $item (a;
    reduce ($item | keys_unsorted[]) as $key (.;
      $item[$key] as $val | ($val | type) as $type | .[$key] = if ($type == "object") then
        deepmerge({}; [if .[$key] == null then {} else .[$key] end, $val])
      elif ($type == "array") then
        (.[$key] + $val | unique)
      else
        $val
      end)
    );
  deepmerge({}; .)'

mkdir -p out
jq -s "$SCRIPT" *.json variants/openldap/*.json > out/openldap.json
jq -s "$SCRIPT" *.json variants/msad/*.json > out/msad.json
jq -s "$SCRIPT" *.json variants/standalone/*.json > out/standalone.json