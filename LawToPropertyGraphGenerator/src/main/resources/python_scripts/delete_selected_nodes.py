import os
import xml.etree.ElementTree as ET

GRAPHML_NS = "http://graphml.graphdrawing.org/xmlns"
YFILES_NS = "http://www.yworks.com/xml/graphml"
NS = {'g': GRAPHML_NS, 'y': YFILES_NS}

ET.register_namespace('', GRAPHML_NS)
ET.register_namespace('y', YFILES_NS)


RESOURCES_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUTPUT_DIR  = os.path.join(RESOURCES_DIR, 'output')

INPUT_FILE = os.path.join(OUTPUT_DIR, 'merged.graphML')
LABELS_FILE = os.path.join(OUTPUT_DIR, 'nodesToRemove.txt')
OUTPUT_FILE = os.path.join(OUTPUT_DIR, 'cleaned.graphML')

# Load node IDs to remove
with open(LABELS_FILE, 'r', encoding='utf-8') as f:
    node_ids_to_remove = {line.strip() for line in f if line.strip()}

# Parse the GraphML
tree = ET.parse(INPUT_FILE)
root = tree.getroot()
graph = root.find('g:graph', NS)

# Build node ID -> node element map
all_nodes = {node.get('id'): node for node in graph.findall('g:node', NS)}

# Remove selected nodes
for node_id in node_ids_to_remove:
    node_elem = all_nodes.get(node_id)
    if node_elem is not None:
        graph.remove(node_elem)

# Rebuild remaining node ID set
remaining_node_ids = set(all_nodes.keys()) - node_ids_to_remove

# Remove edges connected only to removed nodes
for edge in list(graph.findall('g:edge', NS)):
    src = edge.get('source')
    tgt = edge.get('target')
    if src not in remaining_node_ids or tgt not in remaining_node_ids:
        graph.remove(edge)

# Save updated GraphML
tree.write(OUTPUT_FILE, encoding='utf-8', xml_declaration=True)
print("[SUCCESS] cleaned.graphML written with specified nodes and relevant edges removed.")
