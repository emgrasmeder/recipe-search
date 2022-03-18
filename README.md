# recipe-search

Find a recipe based on your search terms

## Requirements

This project is set up with [leiningen](https://leiningen.org/) and since I ran out of time, I didn't figure out how to run `java -jar`, so I think you can't get around having to use lein.

## Usage

### Setup
There's a little bit of preprocessing to do, this can be accomplished by running:

`lein run setup`

### Running
To interface with the simple CLI, you run:

`lein run`

Type in a search term, and push enter. That's it.


## Discussion

I don't think a CLI is the right tool for this, so although I would usually make a CLI tool with an interface like:
`echo "tomato soup" | bb search.clj`, but since time is one of the explicit things we're optimizing for, neither babashka nor a process that needs to start up each time is appropriate.
Ideally, instead, the `do-search` function would be triggered by an API request and the process would already be alive. 

I started working with TDD, but about when I hit the 6 hour mark, I decided to try to sprint for some finish line and abandoned them. The tests are broken because they didn't keep up with refactors I was making. 

## License

Copyright © 2022 Em Grasmeder

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
