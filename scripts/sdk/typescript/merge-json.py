import json
import argparse
import sys

from jsonpath_ng import parse, jsonpath


def _load(path: str) -> object:
    with open(path, 'r') as f:
        return json.loads(f.read())


def _save(outfile, obj):
    if isinstance(outfile, str):
        with open(outfile, 'w') as f:
            json.dump(obj, f, indent=2)
    else:
        json.dump(obj, outfile, indent=2)


def _index_value(index_obj):
    value = getattr(index_obj, 'index', getattr(index_obj, 'idx', None))
    if value is not None:
        return value
    indices = getattr(index_obj, 'indices', None)
    if isinstance(indices, tuple) and len(indices) == 1:
        return indices[0]
    return None


def _iterate(obj, path=''):
    if isinstance(obj, dict):
        for k, v in obj.items():
            new_path = f"{path}['{k}']" if path else k
            yield from _iterate(v, new_path)
    elif isinstance(obj, list):
        for index, item in enumerate(obj):
            new_path = f'{path}[{index}]'
            yield from _iterate(item, new_path)
    else:
        yield path, obj

def _fields(obj):
    if isinstance(obj, jsonpath.Fields):
        for key in obj.fields:
            yield key
    elif isinstance(obj, jsonpath.Index):
        yield _index_value(obj)
    elif isinstance(obj, jsonpath.Child):
        yield from _fields(obj.left)
        yield from _fields(obj.right)
    else:
        print('Not supported yet.', file=sys.stderr)
        exit(-3)


def _merge(obj, updates):
    for path, value in _iterate(updates):
        jsonpath_expr = parse(path)
        found = False
        for match in jsonpath_expr.find(obj):
            if isinstance(match.path, jsonpath.Fields):
                field = match.path.fields[0]
            elif isinstance(match.path, jsonpath.Index):
                field = _index_value(match.path)
                if field is None:
                    print('Not supported yet.', file=sys.stderr)
                    exit(-2)
            else:
                print('Not supported yet.', file=sys.stderr)
                exit(-2)
            parent = match.context.value
            parent[field] = value
            found = True

        if not found:
            fields = list(_fields(jsonpath_expr))
            elem = obj
            for i, field in enumerate(fields[:-1]):
                next_field = fields[i + 1]
                child_template = [] if isinstance(next_field, int) else {}

                if isinstance(field, int):
                    if not isinstance(elem, list):
                        print('Not supported yet.', file=sys.stderr)
                        exit(-2)
                    while len(elem) <= field:
                        elem.append(child_template.copy() if isinstance(child_template, list) else {})
                    if not isinstance(elem[field], (dict, list)):
                        elem[field] = child_template.copy() if isinstance(child_template, list) else {}
                    elem = elem[field]
                else:
                    if not isinstance(elem, dict):
                        print('Not supported yet.', file=sys.stderr)
                        exit(-2)
                    if field not in elem or not isinstance(elem[field], (dict, list)):
                        elem[field] = child_template.copy() if isinstance(child_template, list) else {}
                    elem = elem[field]

            last_field = fields[-1]
            if isinstance(last_field, int):
                if not isinstance(elem, list):
                    print('Not supported yet.', file=sys.stderr)
                    exit(-2)
                while len(elem) <= last_field:
                    elem.append(None)
            elif not isinstance(elem, dict):
                print('Not supported yet.', file=sys.stderr)
                exit(-2)
            elem[last_field] = value

    return obj


def main(i, r, o):
    obj = _load(i)
    updates = _load(r)
    obj = _merge(obj, updates)
    _save(o if o else sys.stdout, obj)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-i', '--input', action='extend', nargs='+')
    parser.add_argument('-o', '--output')

    args = parser.parse_args()

    if not args.input or len(args.input) != 2:
        print('Oops...should have 2 input files', file=sys.stderr)
        exit(-1)

    main(args.input[0], args.input[1], args.output)
