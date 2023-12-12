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
    elif isinstance(obj, jsonpath.Child):
        yield from _fields(obj.left)
        yield from _fields(obj.right)
    else:
        print(sys.stderr, 'Not supported yet.')
        exit(-3)


def _merge(obj, updates):
    for path, value in _iterate(updates):
        jsonpath_expr = parse(path)
        found = False
        for match in jsonpath_expr.find(obj):
            if isinstance(match.path, jsonpath.Fields):
                field = match.path.fields[0]
            elif isinstance(match.path, jsonpath.Index):
                field = match.path.index
            else:
                print(sys.stderr, 'Not supported yet.')
                exit(-2)
            parent = match.context.value
            parent[field] = value
            found = True

        if not found:
            elem = obj
            last_field = None
            for field in _fields(jsonpath_expr):
                if last_field:
                    elem = elem[last_field]
                if field not in elem:
                    elem[field] = {}
                last_field = field

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
        print(sys.stderr, 'Oops...should be 2 input files')
        exit(-1)

    main(args.input[0], args.input[1], args.output)
