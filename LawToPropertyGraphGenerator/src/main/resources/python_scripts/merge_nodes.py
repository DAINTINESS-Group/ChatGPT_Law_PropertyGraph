import os
import xml.etree.ElementTree as ET
import json
from collections import defaultdict

# Constants for namespaces
GRAPHML_NS = "http://graphml.graphdrawing.org/xmlns"
YFILES_NS  = "http://www.yworks.com/xml/graphml"
NS = {
    'g': GRAPHML_NS,
    'y': YFILES_NS
}

# Preserve namespaces on output
ET.register_namespace('', GRAPHML_NS)
ET.register_namespace('y', YFILES_NS)

# Paths
RESOURCES_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
# Now define input and output under resources/output
OUTPUT_DIR  = os.path.join(RESOURCES_DIR, 'output')
INPUT_FILE        = os.path.join(OUTPUT_DIR, 'cleaned.graphML')
OUTPUT_FILE       = os.path.join(OUTPUT_DIR, 'final.graphML')
NORMALIZATION_MAP = os.path.join(OUTPUT_DIR, 'normalizationMap.json')
TIER4_PATH        = os.path.join(OUTPUT_DIR, 'tier4.txt')

# Load normalization map
with open(NORMALIZATION_MAP, 'r', encoding='utf-8') as f:
    norm_map = json.load(f)

# Parse GraphML
tree = ET.parse(INPUT_FILE)
root = tree.getroot()
graph = root.find('g:graph', NS)

# === PHASE 1: Normalize and Deduplicate Nodes ===
normalized_nodes = {}
node_elements_to_remove = []

for node in graph.findall('g:node', NS):
    old_id = node.get('id')
    new_id = norm_map.get(old_id, old_id)

    if new_id not in normalized_nodes:
        normalized_nodes[new_id] = node
        node.set('id', new_id)
        label = node.find('.//y:NodeLabel', NS)
        if label is not None:
            label.text = new_id
    else:
        node_elements_to_remove.append(node)

for node in node_elements_to_remove:
    graph.remove(node)

# === PHASE 2: Normalize and Deduplicate Edges ===
seen_edges = set()
edge_elements_to_remove = []

for edge in graph.findall('g:edge', NS):
    src = norm_map.get(edge.get('source'), edge.get('source'))
    tgt = norm_map.get(edge.get('target'), edge.get('target'))
    key = (src, tgt)
    if key in seen_edges:
        edge_elements_to_remove.append(edge)
    else:
        seen_edges.add(key)
        edge.set('source', src)
        edge.set('target', tgt)

for edge in edge_elements_to_remove:
    graph.remove(edge)

# === PHASE 3.1: Remove Disconnected Nodes ===
node_ids = {node.get('id') for node in graph.findall('g:node', NS)}
edge_map = defaultdict(set)
for edge in graph.findall('g:edge', NS):
    source = edge.get('source')
    target = edge.get('target')
    edge_map[source].add(target)
    edge_map[target].add(source)

disconnected_nodes = {nid for nid in node_ids if nid not in edge_map}
for node in graph.findall('g:node', NS):
    if node.get('id') in disconnected_nodes:
        graph.remove(node)

# === PHASE 3.2: Cascade Orphan Removal (First Pass) ===
removed = set(disconnected_nodes)
changed = True
while changed:
    changed = False
    for node in list(graph.findall('g:node', NS)):
        node_id = node.get('id')
        if node_id in removed:
            continue
        neighbors = edge_map.get(node_id, set()) - removed
        if not neighbors:
            graph.remove(node)
            removed.add(node_id)
            changed = True

# === PHASE 3.3: Remove Tier 4 Nodes That Are Only Weak Targets ===
with open(TIER4_PATH, 'r', encoding='utf-8') as f:
    tier4_ids = set(line.strip() for line in f)

nodes = {node.get('id'): node for node in graph.findall('g:node', NS)}
edges = list(graph.findall('g:edge', NS))
inbound = defaultdict(list)
outbound = defaultdict(list)

for edge in edges:
    source = edge.get('source')
    target = edge.get('target')
    outbound[source].append(edge)
    inbound[target].append(edge)

for node_id in list(nodes.keys()):
    if node_id in tier4_ids and node_id not in outbound:
        incoming = inbound.get(node_id, [])
        if len(incoming) == 1:
            src = incoming[0].get('source')
            if src in tier4_ids:
                graph.remove(nodes[node_id])
                graph.remove(incoming[0])

# === PHASE 3.4: Re-run Cascade Orphan Removal After Tier 4 Edge Pruning ===
updated_edge_map = defaultdict(set)
for edge in graph.findall('g:edge', NS):
    src = edge.get('source')
    tgt = edge.get('target')
    updated_edge_map[src].add(tgt)
    updated_edge_map[tgt].add(src)

removed = set()
changed = True
while changed:
    changed = False
    for node in list(graph.findall('g:node', NS)):
        node_id = node.get('id')
        if node_id in removed:
            continue
        neighbors = updated_edge_map.get(node_id, set()) - removed
        if not neighbors:
            graph.remove(node)
            removed.add(node_id)
            changed = True

# === DONE: Write Output ===
tree.write(OUTPUT_FILE, encoding='utf-8', xml_declaration=True)
print("[SUCCESS] final.graphML written with normalization and pruning.")
